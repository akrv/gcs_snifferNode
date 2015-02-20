/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gcs_sniffernode;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

//usb4java
//
import org.usb4java.ConfigDescriptor;
import org.usb4java.Context;
import org.usb4java.DescriptorUtils;
import org.usb4java.Device;
import org.usb4java.DeviceDescriptor;
import org.usb4java.DeviceHandle;
import org.usb4java.DeviceList;
import org.usb4java.LibUsb;
import org.usb4java.LibUsbException;


public class Gcs_snifferNode {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
    // dont touch my variables
    DeviceHandle handle = new DeviceHandle();
    short idVendor=0x0451;
    short idProduct=0x16ae;
    
    
    short vendorId=0x0451;
    short productId=0x16ae;

    short DEFAULT_CHANNEL = 0x0b; //# 11 

    short DATA_EP = 0x83;
    short DATA_TIMEOUT = 2500;

    short DIR_OUT = 0x40;
    short DIR_IN  = 0xc0;

    short GET_IDENT = 0xc0;
    short SET_POWER = 0xc5;
    short GET_POWER = 0xc6;

    short SET_START = 0xd0 ;//# bulk in starts
    short SET_STOP  = 0xd1 ;//# bulk in stops
    short SET_CHAN  = 0xd2 ;//# 0x0d (idx 0) + data)0x00 (idx 1)
    
    //Absolute hazelcasting
        
//        Config cfg = new Config();
//        HazelcastInstance instance = Hazelcast.newHazelcastInstance(cfg);
//        Map<String, String> mapSniffer = instance.getMap("mapSniffer");
//        Map<String, String> macAddr = instance.getMap("macAddr");
//        Map<String, String> gatewayNames = instance.getMap("gatewayNames");
    
    
    // Create the libusb context
    final Context context = new Context();
    // Initialize the libusb context
    int result = LibUsb.init(context);
    if (result < 0)
    {
        throw new LibUsbException("Unable to initialize libusb", result);
    }
        // Read the USB device list
    final DeviceList list = new DeviceList();
    result = LibUsb.getDeviceList(context, list);
    if (result < 0)
    {
        throw new LibUsbException("Unable to get device list", result);
    }
    
    try
    {
    // Iterate over all devices and dump them
        for (Device device: list)
        {
            // Read the device descriptor
            final DeviceDescriptor descriptor = new DeviceDescriptor();
            result = LibUsb.getDeviceDescriptor(device, descriptor);
            if (result < 0)
            {
                throw new LibUsbException("Unable to read device descriptor",
                result);
            }
            
            byte iSerialNumber = descriptor.iSerialNumber();
            short idVendorDevice = descriptor.idVendor();
            short idProductDevice = descriptor.idProduct();
            if(idVendor == idVendorDevice && idProduct == idProductDevice)
            {
                if(iSerialNumber == 0)
                {
                    System.out.println("thats ma device, why wait? open it!");
                    
                    result = LibUsb.open(device, handle);
                    if (result < 0)
                    {
                        System.out.println(String.format("Unable to open device: %s. "
                            + "Continuing without device handle.",
                        LibUsb.strError(result)));
                        handle = null;
                    }
                    
                }
            }
            
            
        }
    }
    finally
    {
        // Ensure the allocated device list is freed
        LibUsb.freeDeviceList(list, true);
    }
    
    
    
    // Close the device if it was opened
    if (handle != null)
    {
        LibUsb.close(handle);
    }
    // Deinitialize the libusb context
    LibUsb.exit(context);

    
    
}  
}
