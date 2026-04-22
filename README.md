# banking-system

## How to run

1. Clone the repository
2. Run `docker-compose up` in the root directory of the project
3. The application will be available at `http://localhost:8080`
4. To see the OpenApi documentation, go to `http://localhost:8080/swagger-ui/index.html`

## Important choices

What i made...

## Estimation on how many transactions application can handle per second

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

I also used AIs help for writing tests. As I was not that familiar with the new way of writing integration
tests in Spring Boot 4.0.x as I had prior knowledge in 3.0.x. I wrote down my own test ideas, then had AI
generate more edge cases and help me quickly write those out.