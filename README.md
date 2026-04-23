# banking-system

## How to run

1. Clone the repository
2. Run `docker-compose up -d` in the root directory of the project
3. The application will be available at `http://localhost:8080`
4. To see the OpenApi documentation, go to `http://localhost:8080/swagger-ui/index.html`

## Important choices

I decided to split the system into three databases: one for accounts, one for transactions, and one for balances. I did this so things are more organized.
Instead of calculating the balance from all transactions every time, I store the balance directly. This makes it much faster to get the current balance.
For the important information like currency, direction and country I decided to have enums so that it is easier to maintain and validate.
I added a global exception handler so I don’t have to handle errors in every controller. It keeps the code cleaner and makes sure all errors are returned in a consistent way.
I also decided to add openApi documentation to make it easier for people to test my application and understand how to use it.

I focused on keeping things simple and to show that the application can be scalable.

## Estimation on how many transactions application can handle per second

When done some testing then my system can handle around 500 transactions per second. From that point the request start taking a lot longer to happen.

## What to consider to be able to scale applications horizontally

Should consider running multiple instances of the application and definitely have to consider how to handle state in the application. Should be stateless so all state is kept in a shared
database. It is also important to handle concurrency and ensure data consistency (when running multiple instances then need to make sure they dont edit the same data at the same time). Also when using only
a single database then that could become a bottleneck, so it is important to consider how to scale that. 

## How I used AI

For this project, I mainly wanted to showcase my own skills so most of AI usage was not for writing code but
for learning the technologies and reasoning about my ideas.

One use case for AI is that I first familiarized myself with the technologies that I was not that familiar with,
such as myBatis and RabbitMQ. I used my prior knowledge of Liquibase to quickly understand how myBatis works.

One main use case of AI was to help me reason with my architectural decisions. Often I found myself
talking with AI about the pros and cons of my different approaches.

Also used AI to find out how I could test how many transactions my application can handle per second.

I also used AIs help for writing tests. As I was not that familiar with the new way of writing integration
tests in Spring Boot 4.0.x as I had prior knowledge in 3.0.x. I wrote down my own test ideas, then had AI
generate more edge cases and help me quickly write those out.