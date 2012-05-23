(: Graphr - returns data for top tags :)
(: input format: Graphr DB top_tags (graphr_db_tags.xsd) :)
(: output format: Graphr API top_tags (JSON object) :)

(: textual value of tag to return :)
declare variable $value as xs:string external;

(: which method of score should be used :)
declare variable $method as xs:string external;

let $all_tags := collection("tags-week")//tag[@value = $value]
let $single_result := 
for $tag in $all_tags
	let $score := if ($method = "our") then $tag/@ourScore else $tag/@theirScore
	return concat("
    {
      ""tag"": """,$tag/@value,""",
      ""date"": """,$tag/../@date,""",
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