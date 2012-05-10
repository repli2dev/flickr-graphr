(: Graphr - returns data for given users in given range date :)
(: input format: Graphr DB best_people (graphr_best_people.xsd) :)
(:               Graphr DB users (graphr_users.xsd) :)
(: output format: Graphr API best_people (JSON object) :)

(: begin date for in string form YYYY-MM-DD :)
declare variable $begin_date as xs:string external;

(: end date for in string form YYYY-MM-DD :)
declare variable $end_date as xs:string external;

(: user ID to retrieve data for :)
declare variable $requested_userid as xs:string external;

(: 
returns true, if given date is between dates set in $begin_date and $end_date
  @param $date   date to check in string form YYYY-MM-DD
  @return        true, if $begin_date <= $date <= $end_date
  	             false, otherwise
:)
declare function local:in_range($date_str as xs:string) as xs:boolean {	
  let $date := xs:date($date_str)
  let $begin_date_as_date := xs:date($begin_date)
  let $end_date_as_date := xs:date($end_date)
  return $begin_date_as_date <= $date and $date <=  $end_date_as_date
};

let $requested_userid := "32113531@N05"

let $all_people := collection("best_people")/best-people
let $single_result := 
  for $date in $all_people/@date[local:in_range(.)]
	let $score := max((0,$all_people[@date=$date]/person[@person-id=$requested_userid]/@score))
	return concat("
    {
      ""userid:"": """,$requested_userid,""",
      ""username:"": """,$requested_userid,""",
      ""date"": """,$date,""",
      ""score"": ",$score,"
    }")

return
concat(
"{
  ""stat"": ""ok"",
  ""data"": [",
string-join($single_result,","),"
  ]
}")