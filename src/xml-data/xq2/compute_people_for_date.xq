(: Graphr - computes best people for given date:)
(: input format: Graphr DB best_photos (graphr_best_photos.xsd) :)
(: output format: Graphr DB best_people (graphr_best_people.xsd) :)

(: date to process in string form YYYY-MM-DD :)
declare variable $date_processed as xs:string external;

(: number of days to consider - coefficients are altered accordingly :)
declare variable $max_delay as xs:integer external;

(: database file to store the result to -- must exist! :)
(: filename in format "<db-name>/<path-to-doc>" :)
declare variable $output_file as xs:string external;

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

replace node doc($output_file)/* with

<best-people date="{$date_processed}">
{
  let $photos_in_range := collection("graphr/best_photos")/best-photos[local:delay($date_processed,@date) < $max_delay]/photo
  for $photo in $photos_in_range
    let $delay := local:delay($date_processed,$photo/../@date)
    let $qualified_score := $photo/@score * ($max_delay - $delay)
    let $person_id := $photo/@ownerid
  group by $person_id
  return <person person-id="{$person_id}" score="{sum($qualified_score)}"/>
}
</best-people>
