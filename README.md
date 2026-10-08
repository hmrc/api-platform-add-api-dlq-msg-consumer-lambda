
# api-platform-add-api-dlq-msg-consumer-lambda

This repository holds the code for a lambda which is employed to consume messages placed on the DLQ (api_platform_admin_api_add_dead
) used during the process of adding or updating an API.
Where this fails, the details, in the form of JSON, are placed on the DLQ and by ingesting the message we can provide the name of the
API and reason for the failure in publishing. 

Once notified of a message on the DLQ, this lambda publishes a message summarising its contents
to the SNS topic created for raising PagerDuty alerts.

At the time of writing, there's no official PagerDuty documentation explaining how to construct
ad-hoc messages. Their assumption seems to be that you'll create a Cloudwatch metric alarm and have that publish to the 
SNS topic upon firing.

The code constructing the SNS message in this repo is based on https://blog.cetinich.net/content/2022/cloudwatch-pagerduty-sns/ which 
advises that the mandatory part of the message is pretty simple: there must be a subject containing ```ALARM: <your choice of subject>```
and a JSON body containing at minimum ```{"NewStateValue": "ALARM"}```
Be careful making changes to the structure of the message output to SNS as invalid messages vanish without trace or error.
In the aforementioned blog post, a sample of an entire message from SNS to PagerDuty is shown. This includes a signing key 
and the URL of the certificate to use for verifying the signature. It may explain why trying to cURL PagerDuty doesn't result in the 
creation of an incident there.
### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").

