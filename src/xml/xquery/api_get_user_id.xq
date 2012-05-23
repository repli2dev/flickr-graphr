(: Graphr - returns user IDs for given displayName :)
(: input format: Graphr DB users (graphr_db_users.xsd) :)
(: output format: String (userID or empty string) :)

(: display name to look for in the database :)
declare variable $requested_display_name as xs:string external;

let $all_users := collection("users")/users/user
let $user_id := distinct-values($all_users[@display-name=$requested_display_name]/@user-id)[1]
return $user_id