(: Graphr - returns interesting tags (those who have multiple day entries) :)
(: input format: Graphr DB top_tags (graphr_db_tags.xsd) :)
(: output format: Graphr API interesting tags (JSON object) :)

(: Position in tags (0 equals start) :)
declare variable $start as xs:integer external;

(: Number of tags returned :)
declare variable $records as xs:integer external;

(: Do query with usage of group function COUNT :)
let $allTagsWithCount := 
	for $uniqueTag in distinct-values(collection("tags-week")//tag/@value)
		let $items := collection("tags-week")//tag[@value = $uniqueTag ]
		order by count($items) descending
		return <tag value="{$uniqueTag}" count="{count($items)}" />

(: Limit number of results and transform it into JSON :)
let $single_result := 
	for $tag in subsequence($allTagsWithCount,$start,$records)
	return concat("{
      ""tag"": """,$tag/@value,"""
    }")

return
concat(
"{
  ""stat"": ""ok"",
  ""data"": [",
string-join($single_result,","),"
  ]
}")