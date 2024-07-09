Assumptions:

1. An individual account can not be upgraded to business owner account if number of orders within last 1 year from current date is not greater than equal to 10.

2. Sub account can not become business account directly, if he wants to then he needs to first delink from associated business account.

3. A Business Owner account cannot add another sub account of another business account or another business Owner account.

4. Assuming Business Owner and SubAccount users both will pass SubAccount id only as parameter to delink from business account.

5. Business owner account can not downgrade from Business owner account to Individual Account.

6. Only the self SubAccount user or its parent user can de link account

7. Assuming Business owner always wants to get orders of Sub account users linked to his business account

8. User name should be unique for each account
9. A business account owner can send as many invitations to Individual account owner until the account type changes to SubAccount
10. Any of the invitation id if not expired can be used to link the account to Business owner account 
11. Only the person who was actually invited having Individual account only would be able to perform the action 
12. Invitation gets expired after 24 hours and can not be used to perform action on it
13. If a user had multiple invitations and rejected an invitation and again accepts the other unexpired invitation, it will be treated as accept
