
# How to run

	mvn spring-boot:run

# How to test users from cmd line

	http -vj POST localhost:8080/users firstName=Foo lastName=Bar email=foo@bar.com password=foobar

	http -vj POST localhost:8080/users firstName=Foo lastName=Baz email=foo@baz.com password=foobaz

	http -vj POST localhost:8080/auth email=foo@bar.com password=foobar

	http -vj POST localhost:8080/auth email=foo@baz.com password=foobaz

	http -vj GET  localhost:8080/me 'Authorization: Bearer <JWT>'

	http -vj POST localhost:8080/logout 'Authorization: Bearer <JWT>'

# How to test files from cmd line

	http -vj POST localhost:8080/files name=foo.txt contents=Zm9v 'Authorization: Bearer <JWT>'

	http -vj POST localhost:8080/files name=bar.txt contents=YmFy 'Authorization: Bearer <JWT>'

	http -vj POST localhost:8080/files name=baz.txt contents=YmF6 'Authorization: Bearer <JWT>'

	http -vj GET localhost:8080/files 'Authorization: Bearer <JWT>'

	http -vj DELETE localhost:8080/files/{fileId} 'Authorization: Bearer <JWT>'

	http -v GET localhost:8080/files/{fileId}/contents 'Authorization: Bearer <JWT>'
