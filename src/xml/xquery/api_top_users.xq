(: Graphr - returns top user IDs for given date :)
(: input format: Graphr DB top-users (graphr_db_top_users.xsd) :)
(: output format: Graphr API top_users (JSON object) :)

(: date in string form YYYY-MM-DD :)
declare variable $date as xs:string external;

(: amount of top IDs to return :)
declare variable $count as xs:integer external;

let $all_users := collection("top-users")/top-users[@date=$date]
let $single_result := 
  for $userid in $all_users/user[position() <= $count]/@user-id
	return concat("
    {
      ""userId:"": """,$userid,""",
      ""date"": """,$date,""",
    }")

return
concat(
"{
  ""stat"": ""ok"",
  ""data"": [",
string-join($single_result,","),"
  ]
}")