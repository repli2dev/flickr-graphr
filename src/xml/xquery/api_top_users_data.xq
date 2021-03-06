(: Graphr - returns data for given users in given range date :)
(: input format: Graphr DB top-users (graphr_db_top_users.xsd) :)
(:               Graphr DB users (graphr_db_users.xsd) :)
(: output format: Graphr API top_users (JSON object) :)

(: begin date in string form YYYY-MM-DD :)
declare variable $begin_date as xs:string external;

(: end date in string form YYYY-MM-DD :)
declare variable $end_date as xs:string external;

(: user ID to retrieve data for :)
declare variable $requested_userid as xs:string external;

(:
escapes quotes and slashes in given string
  @param $string  string to escape
  @return         escaped version of input
:)
declare function local:escape($string as xs:string) as xs:string {
let $mid_result := replace($string,"""","\\""")
let $mid_result2 := replace($mid_result,"/","\\/")
return $mid_result2
};

(: 
returns true, if given date is between dates set in $begin_date-$extend_to_past and $end_date
  @param $date  				 date to check in string form YYYY-MM-DD
  @param $extend_to_past how many days before $begin_date should be checked
  @return       				 true, if $begin_date <= $date <= $end_date
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
	let $display_name_db := distinct-values(collection("users")/users/user[@user-id=$requested_userid]/@display-name)[1]
	let $display_name := if (empty($display_name_db)) then $requested_userid else $display_name_db
	return concat("
    {
      ""userId"": """,$requested_userid,""",
      ""displayName"": """,local:escape($display_name),""",
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