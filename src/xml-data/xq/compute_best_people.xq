(: computing best people from best photos :)

<best-people>
    {
    let $max_delay := 7 (: exclusive! :)
    let $best_photos := doc("photos-src/best_photos.xml")//photo
    
    for $person_id in distinct-values($best_photos/@ownerid)
    let $max_day := 24
    let $score := sum(for $photo in $best_photos
                          let $delay := $max_day - day-from-date($photo/@date)
                          where $photo/@ownerid=$person_id and $delay < $max_delay and $delay >= 0
                          return ($max_delay - $delay) * $photo/@score)
    order by $score descending
    return <person personid="{$person_id}"
                   date="2012-04-{$max_day}"
                   score="{$score}" />
    }
</best-people>