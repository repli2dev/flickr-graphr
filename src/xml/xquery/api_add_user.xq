(: Graphr - adds given id/displayName pair to database to give date :)
(: input format: Graphr DB users (graphr_db_users.xsd) :)
(: output format: none :)

(: user ID :)
declare variable $user_id as xs:string external;

(: user display name :)
declare variable $display_name as xs:string external;

(: date in string form YYYY-MM-DD :)
declare variable $date as xs:string external;

db:output("User successfully added."),
let $data_for_day := collection("users")/users[@date = $date]
let $final_target := if (empty($data_for_day)) then collection("users")[last()]/users
                                               else $data_for_day 
let $need_to_add := empty($final_target/user[@user-id = $user_id and @display-name = $display_name])
let $value_to_add := if ($need_to_add) then <user user-id="{$user_id}" display-name="{$display_name}"/>
                                       else ""
return 
insert node $value_to_add as first into $final_target