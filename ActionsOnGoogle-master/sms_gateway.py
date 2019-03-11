import json
import boto3 
from boto3.dynamodb.conditions import Key, Attr

def lambda_handler(event, context):
    
    client = boto3.client(
    "sns",
    aws_access_key_id="--access-key--",
    aws_secret_access_key="--secrete--",
    region_name="us-east-1"
    )
    
    phone_number = event['number'];
    
    client.publish(
                 PhoneNumber=phone_number,
                 Message=event['message'],
                    MessageAttributes={
            'AWS.SNS.SMS.SenderID': {
                'DataType': 'String',
                'StringValue': 'SENDERID'
            },
            'AWS.SNS.SMS.SMSType': {
                'DataType': 'String',
                'StringValue': 'Transactional'
             }
               }
            )  
    return "Data with message is succesfully sent to required person."
    
