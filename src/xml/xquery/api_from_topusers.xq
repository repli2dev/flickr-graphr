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
declare function local:in_range($date_str as xs:string, $extend_to_past as xs:integer) as xs:boolean {
	let $duration := $extend_to_past * (xs:date("2010-01-02")-xs:date("2010-01-01"))
  let $date := xs:date($date_str)
  let $begin_date_as_date := xs:date($begin_date) - $duration
  let $end_date_as_date := xs:date($end_date)
  return $begin_date_as_date <= $date and $date <=  $end_date_as_date
};

let $all_users := collection("top-users")/top-users[local:in_range(@date,0)]
let $single_result := 
  for $date in $all_users/@date
	let $score := max((0,$all_users[@date=$date]/user[@user-id=$requested_userid]/@score))
	let $display_name_db := distinct-values(collection("users")/users[local:in_range(@date,7)]/user[@user-id=$requested_userid]/@display-name)[1]
	let $display_name := if (empty($display_name_db)) then "name unknown" else $display_name_db
	return concat("
    {
      ""user-id:"": """,$requested_userid,""",
      ""display-name:"": """,$display_name,""",
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
