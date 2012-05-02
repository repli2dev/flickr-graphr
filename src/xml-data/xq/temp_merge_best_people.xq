(: putting best people together :)

<best-people>
    {
    for $person in doc("people-src/best_people_2012-04-30.xml")//person |
                   doc("people-src/best_people_2012-04-29.xml")//person |
                   doc("people-src/best_people_2012-04-28.xml")//person |
                   doc("people-src/best_people_2012-04-27.xml")//person |
                   doc("people-src/best_people_2012-04-26.xml")//person |
                   doc("people-src/best_people_2012-04-25.xml")//person |
                   doc("people-src/best_people_2012-04-24.xml")//person
    return $person
    }
</best-people>
