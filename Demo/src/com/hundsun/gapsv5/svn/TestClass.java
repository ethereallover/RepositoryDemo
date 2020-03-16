package com.hundsun.gapsv5.svn;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.hundsun.gapsv5.svn.inter.TestInterFace;

public class TestClass extends SVNUtil implements TestInterFace {

	public TestClass(String userName, String password) {
		super(userName, password);

	}

	public static void getConnection() {
		Connection conn = null;
		try {
			//Class.forName("com.informix.jdbc.IfxDriver"); // 注册数据库驱动
			Class.forName("oracle.jdbc.driver.OracleDriver");
			//String url = "jdbc:informix-sqli://192.168.58.229:9088/tamc:INFORMIXSERVER=ifxserver;NEWLOACLE=en_us,zh_cn,zh_tw;NEWCODESET=GBK,8859-1,819,Big5;IFX_USE_STRENC=true;"; 
			String url = "jdbc:oracle:thin:@127.0.0.1:1521:COMPAY"; 
			conn = DriverManager.getConnection(url, "compay", "hundsun2018"); // 获取连接数据库的Connection对象
			// DatabaseMetaData实例的获取
			DatabaseMetaData metaData = conn.getMetaData();
			// 获取数据库的名字
			System.out.println("获取数据库的名字:" + metaData.getDatabaseProductName());
			// 获取数据库的版本
			System.out.println("获取数据库的版本:" + metaData.getDatabaseProductVersion());
			// 获取数据库的主版本
			System.out.println("获取数据库的主版本:" + metaData.getDatabaseMajorVersion());
			// 获取数据库的小版本
			System.out.println("获取数据库的小版本:" + metaData.getDatabaseMinorVersion());
			
			System.out.println("varchar2(32)(32,)".replaceAll(" ", "")
	                .replaceAll(",0", "").toLowerCase());

			System.out.println(String.format("%s(%s)", "varchar2(32)", "32"));

			Map<String, Map<String, String>> map = new HashMap<String, Map<String, String>>();
			ResultSet columns = metaData.getColumns(conn.getCatalog(), "COMPAY", "TRANS", "%");
			boolean exist = true;
			/*while (columns.next()) {
				getMapByResultSet(columns, map);
				exist = true;
			}*/
			if(exist){
				while(columns.next()){
					System.out.println("COLUMN_NAME:" + columns.getString("COLUMN_NAME"));
					System.out.println("NULLABLE:" + columns.getString("NULLABLE"));
					System.out.println("TYPE_NAME:" + columns.getString("TYPE_NAME"));
					System.out.println("COLUMN_SIZE:" + columns.getString("COLUMN_SIZE"));
					System.out.println("DECIMAL_DIGITS:" + columns.getString("DECIMAL_DIGITS"));
					System.out.println("COLUMN_DEF:" + columns.getString("COLUMN_DEF"));
					System.out.println("REMARKS:" + columns.getString("REMARKS"));
					System.out.println("DATA_TYPE:" + columns.getString("DATA_TYPE"));
					System.out.println("****************************************************");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void getMapByResultSet(ResultSet colRet, Map<String, Map<String, String>> map) throws SQLException {
		Map<String, String> attributes = new HashMap<String, String>();
		String columnName = colRet.getString("COLUMN_NAME");
		attributes.put("NULLABLE", colRet.getString("NULLABLE"));
		attributes.put("TYPE_NAME", colRet.getString("TYPE_NAME"));
		attributes.put("COLUMN_SIZE", colRet.getString("COLUMN_SIZE"));
		attributes.put("DECIMAL_DIGITS", colRet.getString("DECIMAL_DIGITS"));
		attributes.put("COLUMN_DEF", colRet.getString("COLUMN_DEF"));
		attributes.put("REMARKS", colRet.getString("REMARKS"));
		attributes.put("DATA_TYPE", colRet.getString("DATA_TYPE"));
		map.put(columnName.toUpperCase(), attributes);
	}

	private static void getFkeys(){
		try {
			Connection conn = null;
			//Class.forName("com.informix.jdbc.IfxDriver"); // 注册数据库驱动
			Class.forName("oracle.jdbc.driver.OracleDriver");
			//String url = "jdbc:informix-sqli://192.168.58.229:9088/tamc:INFORMIXSERVER=ifxserver;NEWLOACLE=en_us,zh_cn,zh_tw;NEWCODESET=GBK,8859-1,819,Big5;IFX_USE_STRENC=true;"; 
			String url = "jdbc:oracle:thin:@192.168.53.26:1521:oradep3"; 
			conn = DriverManager.getConnection(url, "cnaps20t", "G20_goodbye"); // 获取连接数据库的Connection对象
			DatabaseMetaData metaData = conn.getMetaData();
			
			ResultSet rs = metaData.getImportedKeys(conn.getCatalog(), "CNAPS20T", "BEPS_CIS_RES_R_HIS");
			
			while(rs.next()){
				System.out.println("FK_NAME:"+rs.getString("FK_NAME"));
				System.out.println("FKCOLUMN_NAME:"+rs.getString("FKCOLUMN_NAME"));
				System.out.println("PKTABLE_NAME:"+rs.getString("PKTABLE_NAME"));
				System.out.println("PKCOLUMN_NAME:"+rs.getString("PKCOLUMN_NAME"));
				System.out.println("COLUMN_NAME:"+rs.getString("COLUMN_NAME"));
				System.out.println("CONSTRAINT_NAME:"+rs.getString("CONSTRAINT_NAME"));
				System.out.println("REFERENCED_TABLE_NAME:"+rs.getString("REFERENCED_TABLE_NAME"));
				System.out.println("REFERENCED_COLUMN_NAME:"+rs.getString("REFERENCED_COLUMN_NAME"));
				System.out.println("****************************************");
			}
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void getlocalFileSVNInfoByCommond(){
		//CountDownLatch downLatch = new CountDownLatch(0);
		StringBuffer queryInputResult = new StringBuffer();
		try {
			String commond = "svn info D:\\Eclipse\\eclipse-mars-r2\\runtime-EclipseApplication\\gaps.demo\\src\\main\\resources\\basetable.table";
			Process process = Runtime.getRuntime().exec(commond);
			InputStream inputStream = process.getInputStream();
			byte[] b = new byte[1024];
			while(inputStream.read() > 0){
				inputStream.read(b);
			}
			System.out.println(new String(b,"GB2312"));
			/*BufferedReader bf = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            System.out.println("====================================================");
            while ((line = bf.readLine()) != null && line.length() > 0) {
                System.out.println(new String(line.getBytes(),"GB2312"));
                queryInputResult.append(line).append("\n");
            }*/
            System.out.println("==================================================");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		//getConnection();
		//getFkeys();
		//System.out.println("varchar2(40)(40,)".indexOf("varchar2(40)"+"(") !=-1);
		//getlocalFileSVNInfoByCommond();
		
		Map<String,Map<String,String>> mmap = new HashMap<String,Map<String,String>>();
		mmap.put("a", new HashMap<String,String>());
		
		System.out.println(null == mmap.get("a"));
		System.out.println(mmap.get("a").entrySet().isEmpty());
	}
	// yuyuyuuuyu
}
