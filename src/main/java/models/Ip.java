package models;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.logging.Logger;

import exceptions.NotValidIPException;
import inet.ipaddr.IPAddress;
import inet.ipaddr.IPAddressString;

public class Ip {
	public static final Logger LOGGER = Logger.getLogger(Ip.class.getName());
	private ArrayList<String> ipv4DnsServers;
	private ArrayList<String> ipv6DnsServers;
	private static final String COMMAND = "powershell.exe $ip=Get-NetIPConfiguration; $ip.'DNSServer' | ForEach-Object -Process {$_.ServerAddresses}";
	private String clouflareIp;
	private String googleIp;
	public Ip() {
		try {
			setupArrays();
			parseDnsServersIp();
			getDoHIps();
		} catch (Exception e) {
		}
	}

	private void setupArrays() throws IOException {
		ipv4DnsServers = new ArrayList<String>();
		ipv6DnsServers = new ArrayList<String>();
	}

	private void parseDnsServersIp() throws IOException {
		Process powerShellProcess;
		powerShellProcess = Runtime.getRuntime().exec(COMMAND);
		powerShellProcess.getOutputStream().close();
		String line;
		BufferedReader stdout = new BufferedReader(new InputStreamReader(powerShellProcess.getInputStream()));
		while ((line = stdout.readLine()) != null) {
			if (isIPv4Address(line)) {
				ipv4DnsServers.add(line);
			} else {
				if (isIpv6Address(line) && !line.startsWith("fe"))
					ipv6DnsServers.add(line);
			}
		}
		stdout.close();
	}

	private void getDoHIps() throws UnknownHostException {
		clouflareIp = InetAddress.getByName("cloudflare-dns.com").getHostAddress();
		googleIp = InetAddress.getByName("dns.google").getHostAddress();
	}
	public String getIpv4DnsServer() {
		if (ipv4DnsServers.size() == 0) {
			return "";
		} else {
			return ipv4DnsServers.get(0);
		}
	}

	public String getIpv6DnsServer() {
		if (ipv6DnsServers.size() == 0) {
			return "";
		} else {
			return ipv6DnsServers.get(0);
		}
	}

	public static boolean isIPv4Address(String stringAddress) {
		try {
			IPAddress ip = new IPAddressString(stringAddress).getAddress();
			return ip.isIPv4();
		} catch (Exception e) {
			return false;
		}
	}

	public static boolean isIpv6Address(String stringAddress) {
		try {
			IPAddress ip = new IPAddressString(stringAddress).getAddress();
			return ip.isIPv6();
		} catch (Exception e) {
			return false;
		}

	}

	public static boolean isIpValid(String stringAddress) {
		boolean a = Ip.isIPv4Address(stringAddress);
		boolean b = Ip.isIpv6Address(stringAddress);
		LOGGER.info("is IP :" + stringAddress + "valid-> " + ((a || b)));
		return (a || b);
	}

	public static String getIpReversed(String stringAddress) throws NotValidIPException {
		if (Ip.isIpValid(stringAddress)) {
			return new IPAddressString(stringAddress).getAddress().toReverseDNSLookupString();
		} else {
			throw new NotValidIPException();
		}
	}

	public static String getPrimaryDNSIp() {
		try {
			String[] lineParts = null;
			ProcessBuilder builder = new ProcessBuilder("nslookup");
			builder.redirectErrorStream(true);
			Process p = builder.start();
			BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line;
			while (true) {
				line = r.readLine();
				if (line == null) {
					break;
				}
				if (line.contains("Address:")) {
					lineParts = line.split(":");
					break;
				}

			}
			String res = lineParts[1];
			res = res.replaceAll("\\s", "");
			LOGGER.info("Found primary DNS ip addres");
			return res;
		} catch (Exception e) {
			return "No primary address";
		}
		
		
	}

	public String getClouflareIp() {
		return clouflareIp;
	}

	public String getGoogleIp() {
		return googleIp;
	}
}