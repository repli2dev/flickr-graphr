(: computing best-of-best people from best people :)

<best-people>
    {
    let $number_of_results := 10
    let $max_date_string := "2012-04-30"
    let $best_people := doc("people-src/best_people.xml")//person
    for $person_id in $best_people[position() <= $number_of_results]/@personid
    return <person personid="{$person_id}"/>
    }
</best-people>