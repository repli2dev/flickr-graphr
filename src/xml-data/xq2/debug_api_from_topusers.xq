(: Graphr - computes best users for frontend :)
(: input format: Graphr DB top_users (graphr_top_users.xsd) :)
(:               Graphr DB users (graphr_users.xsd) :)
(: output format: Graphr API top_people (JSON object) :)

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

(: begin date for in string form YYYY-MM-DD :)
(: declare variable $begin_date as xs:string external; :)
let $begin_date := "2012-04-20"

(: end date for in string form YYYY-MM-DD :)
(: declare variable $end_date as xs:string external; :)
let $end_date := "2012-04-20"

(: document of user IDs to retrieve data for :)
(: declare variable $user_doc as xs:string external; :)
(: user IDs are extracted from elemnts 'user', attribute 'user-id' :)
let $user_doc := "/home/martin/Documents/flickr-graphr/src/xml-data/temp2/query_users.xml"

return

<root>{
let $requested_users := doc($user_doc)//user/@user-id
let $all_users := collection("top_users")/top-users
for $user_id in $requested_users, $date in $all_users/@date[local:in_range(.)]
let $score := max((0,$all_users[@date=$date]/user[@user-id=$user_id]/@score))
return <object owner-id="{$user_id}"
               date="{$date}"
               score="{$score}"/>
}</root>