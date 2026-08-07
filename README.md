
# api-platform-add-api-dlq-msg-consumer-lambda

This repository holds the code for a lambda which is employed to consume messages placed on the DLQ (api_platform_admin_api_add_dead
) used during the process of adding or updating an API.
Where this fails, the details, in the form of JSON, are placed on the DLQ and by ingesting the message we can provide the name of the
API and reason for the failure in publishing. 

### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").