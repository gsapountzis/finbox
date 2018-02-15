
# How to run

	mvn package

	mvn spring-boot:run

# How to test from cmd line

	http -vj POST localhost:8080/users firstName=Foo lastName=Bar email=foo@bar.com password=foobar

	http -vj POST localhost:8080/auth email=foo@bar.com password=foobar

	http -vj GET  localhost:8080/me 'Authorization: Bearer <JWT>'

	http -vj POST localhost:8080/logout 'Authorization: Bearer <JWT>'
