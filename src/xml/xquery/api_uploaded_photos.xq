(: Graphr - returns data counts of uploaded photos :)
(: input format: Graphr DB  (graphr_db_uploaded_photos.xsd) :)
(: output format: Graphr API uploaded photos (JSON object) :)

(: begin date in string form YYYY-MM-DD :)
declare variable $begin_date as xs:string external;

(: end date in string form YYYY-MM-DD :)
declare variable $end_date as xs:string external;

let $single_result := 
  for $tag in collection("uploaded-photos")/day[@date >= $begin_date and @date <= $end_date]
	return concat("
    {
      ""date"": """,$tag/@date,""",
      ""score"": ",$tag/@photosCount,"
    }")

return
concat(
"{
  ""stat"": ""ok"",
  ""data"": [",
string-join($single_result,","),"
  ]
}")