(: Graphr - adds given id/displayName pair to database to give date :)
(: input format: Graphr DB users (graphr_db_users.xsd) :)
(: output format: none :)

(: user ID :)
declare variable $user_id as xs:string external;

(: user display name :)
declare variable $display_name as xs:string external;

(: date in string form YYYY-MM-DD :)
declare variable $date as xs:string external;

let $data_for_day := collection("users")/users[@date = $date]
let $final_target := if (empty($data_for_day)) then collection("users")[last()]/users
                                               else $data_for_day 
return 
insert node <user user-id="{$user_id}" display-name="{$display_name}"/>
       as last into $final_target