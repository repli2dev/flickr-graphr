(: Graphr - computes best users for frontend :)
(: input format: Graphr DB best_people (graphr_best_people.xsd) :)
(:               Graphr DB users (graphr_users.xsd) :)
(: output format: Graphr API best_people (? JSON object) :)

(: begin date for in string form YYYY-MM-DD :)
declare variable $begin_date as xs:string external;

(: end date for in string form YYYY-MM-DD :)
declare variable $end_date as xs:string external;

(: begin date for in string form YYYY-MM-DD :)
declare variable $users as item() external;

(: 
returns true, if given date is between dates set in $begin_date and $end_date
  @param $date   date to check in string form YYYY-MM-DD
  @return        true, if $begin_date <= $date <= $end_date
  	             false, otherwise
:)
declare function local:in_range($date_str as xs:string) as xs:boolean {
	let $begin_date := "2012-04-20"
	let $end_date := "2012-04-30"
	
  let $date := xs:date($date_str)
  let $begin_date_as_date := xs:date($begin_date)
  let $end_date_as_date := xs:date($end_date)
  
  return $begin_date_as_date <= $date and $date <=  $end_date_as_date
};

<root>{
let $requested_people := doc("/home/martin/Documents/flickr-graphr/src/xml-data/query_users.xml")//person/@person-id
let $all_people := collection("graphr/best_people")/best-people
for $person_id in $requested_people, $date in $all_people/@date[local:in_range(.)]
let $score := max((0,$all_people[@date=$date]/person[@person-id=$person_id]/@score))
return <object owner-id="{$person_id}"
               date="{$date}"
               score="{$score}"/>
}</root>