(: Graphr - computes top users for given date :)
(: input format: Graphr DB top_photos (graphr_top_photos.xsd) :)
(: output format: Graphr DB top_users (graphr_top_users.xsd) :)

(: date to process in string form YYYY-MM-DD :)
declare variable $date_processed as xs:string external;

(: number of days to consider - coefficients are altered accordingly :)
declare variable $max_delay as xs:integer external;

(: 
computes delay of two dates in string format YYYY-MM-DD
  @param $date_current_str  date FROM which to compute delay
  @param $date_old_str      date OF which to compute delay
  @return                   1000, if $date_old is in future from $date_current
  	                        $delay (always >= 0), otherwise
:)
declare function local:delay($date_current_str as xs:string, $date_old_str as xs:string) as xs:integer {
  let $date_current := xs:date($date_current_str)
  let $date_old := xs:date($date_old_str)
  let $delay := days-from-duration($date_current - $date_old)
  return if ($delay >= 0)
            then $delay
            else 1000 (: possibly 9 223 372 036 854 775 807 :)
};

<top-users date="{$date_processed}">
{
for $user in 
    let $photos_in_range := db:open("top-photos")/top-photos[local:delay($date_processed,@date) < $max_delay]/photo
    for $photo in $photos_in_range
        let $delay := local:delay($date_processed,$photo/../@date)
        let $qualified_score := $photo/@score * ($max_delay - $delay)
        let $user_id := $photo/@user-id
    group by $user_id
    return <user user-id="{$user_id}" score="{sum($qualified_score)}"/> 
order by number($user/@score) descending
return $user
}
</top-users>
