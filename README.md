# Barcode Streams Driver
 A TCP Barcode reader driver for Ignition SCADA Event Streams Module. 
 Adds a TCP listener for barcodes into an Ignition Gateway and sends the received barcodes to Event Streams as a payload. 

## Usage:
- Install the module (.modl) from the releases. 
- Restart the gateway
- Add an event stream with "Barcode Stream Source" as the event source
- Input a port number to listen on (Must be above 1000)
- Set the "Encoder" settings to "String"
- Save the event stream
- Open the firewall on your server on that port number (TCP)
- Test the listener by sending a barcode to the port. 

## Payload
`event.data` will contain the raw ASCII string transmitted.

`event.metadata` will contain the follosing structure:

```
{
 sourceIp: <IP address of sender>
 timestamp: <Epoch timestamp in millis>
}

```


