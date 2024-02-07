#EatWhere

![EatWhere DB](/docs/images/eatwhere_db.png)

### Table eatwhere.session_users 
|Idx |Name |Data Type |
|---|---|---|
| * &#128273;  | id| bigserial  |
| * &#128270; &#11016; | session\_id| bigserial  |
| * &#128270; &#11016; | user\_id| bigserial  |
| * &#128270; | state| varchar(30)  |
|  | restaurant| varchar(100)  |


##### Indexes 
|Type |Name |On |
|---|---|---|
| &#128273;  | pk\_session\_users | ON id|
| &#128270;  | session\_users\_session\_id | ON session\_id|
| &#128270;  | session\_users\_user\_id | ON user\_id|
| &#128270;  | session\_users\_state | ON state|

##### Foreign Keys
|Type |Name |On |
|---|---|---|
|  | fk | ( session\_id ) ref [eatwhere.sessions](#sessions) (id) |
|  | fk | ( user\_id ) ref [eatwhere.users](#users) (id) |




### Table eatwhere.sessions 
|Idx |Name |Data Type |
|---|---|---|
| * &#128273;  &#11019; | id| bigserial  |
| * &#128270; &#11016; | owner\_user\_id| bigserial  |
| * &#128270; | state| varchar(30)  |
| * | start\_date| timestamp  |
|  | end\_date| timestamp  |
|  | restaurant| varchar(100)  |


##### Indexes 
|Type |Name |On |
|---|---|---|
| &#128273;  | pk\_sessions | ON id|
| &#128270;  | sessions\_owner\_user\_id | ON owner\_user\_id|
| &#128270;  | sessions\_state | ON state|

##### Foreign Keys
|Type |Name |On |
|---|---|---|
|  | fk | ( owner\_user\_id ) ref [eatwhere.users](#users) (id) |




### Table eatwhere.users 
|Idx |Name |Data Type |
|---|---|---|
| * &#128273;  &#11019; | id| bigserial  |
| * &#128269; | name| varchar(100)  |


##### Indexes 
|Type |Name |On |
|---|---|---|
| &#128273;  | pk\_users | ON id|
| &#128269;  | unq\_users\_name | ON name|
| &#128270;  | users\_name | ON name|




