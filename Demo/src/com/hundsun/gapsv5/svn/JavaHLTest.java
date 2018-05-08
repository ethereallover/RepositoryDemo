package com.hundsun.gapsv5.svn;

import java.util.Properties;

import org.apache.subversion.javahl.ClientException;
import org.apache.subversion.javahl.SVNClient;
import org.apache.subversion.javahl.types.RuntimeVersion;

public class JavaHLTest {
	
	public static void main(String[] args) throws ClientException{
		SVNClient client = new SVNClient();
		
		String adminDirectoryName = client.getAdminDirectoryName();
		
		System.out.println(adminDirectoryName);
		
		RuntimeVersion runtimeVersion = client.getRuntimeVersion();
		
		System.out.println(runtimeVersion);
		
		System.out.println(client.getConfigDirectory());
		
		Properties properties = System.getProperties();
		String property = properties.getProperty("subversion.native.library");
		System.out.println(property);
		
		System.loadLibrary("libsvnjavahl-1");
	}

}

