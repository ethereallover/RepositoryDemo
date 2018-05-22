package com.hundsun.gapsv5.svn;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNProperties;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.internal.wc.DefaultSVNOptions;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNUpdateClient;

import com.hundsun.gapsv5.svn.bean.ForeignReference;
import com.hundsun.gapsv5.svn.bean.Index;
import com.hundsun.gapsv5.svn.bean.Table;
import com.hundsun.gapsv5.svn.bean.TableField;
import com.hundsun.gapsv5.svn.bean.Tables;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class SVNUtil {
	
	private static final Logger log= Logger.getLogger(SVNUtil.class.getName());
	
	private String userName;
	
	private String password;
	
	private String url;
	
	private SVNClientManager clientManager;
	
	private SVNUpdateClient updateClient;
	
	List<SVNDirEntry> dirs = new ArrayList<SVNDirEntry>();
	
	static {
		DAVRepositoryFactory.setup();
		SVNRepositoryFactoryImpl.setup();
	}
	
	public SVNUtil(String userName,String password){
		init(userName, password);
	}
	
	public SVNUtil(String userName,String password,String url){
		this(userName,password);
		this.url = url;
	}
	
	
	private void init(String userName,String password){
		DefaultSVNOptions options = new DefaultSVNOptions();
		options.setAuthStorageEnabled(false);
		clientManager = SVNClientManager.newInstance(options, userName, password);
		updateClient = clientManager.getUpdateClient();
		updateClient.setIgnoreExternals(false);
	}

	private void getUserInfo(DefaultSVNOptions options) {
		System.out.println(options.getDefaultSSHUserName());
		System.out.println(options.getDefaultSSHPassphrase());
		System.out.println(options.getDefaultSSHKeyFile());
	}
	
	/**获取文档内容
     * @param url
     * @return
     */
    public String checkoutFileToString(String url) throws SVNException {
    	
        SVNRepository repository = createRepository(url);
        SVNDirEntry entry = repository.getDir("", -1, false, null);
        int size = (int)entry.getSize();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(size);
        SVNProperties properties = new SVNProperties();
        repository.getFile("", -1, properties, outputStream);
        String doc = new String(outputStream.toByteArray(), Charset.forName("utf-8"));
        return doc;
    }
    
    private SVNRepository createRepository(String url){
        try {
            return clientManager.createRepository(SVNURL.parseURIEncoded(url), true);
        } catch (SVNException e) {
        	log.log(Level.WARNING,"createRepository error");
        }
        return null;
    }
    
    /**检查路径是否存在
     * @param url
     * @return 1：存在    0：不存在   -1：出错
     */
    public int checkPath(String url){
        SVNRepository repository = createRepository(url);
        SVNNodeKind nodeKind;
        try {
            nodeKind = repository.checkPath("", -1);
            boolean result = nodeKind == SVNNodeKind.NONE ? false : true;
            if(result){
            	return 1;
            }
        } catch (SVNException e) {
            log.log(Level.WARNING,"checkPath error");
            return -1;
        }
        return 0;
    }
    
    /**列出指定SVN 地址目录下的子目录
     * @param url
     * @return
     */
    public List<SVNDirEntry> listFolder(String url){
        if(checkPath(url)==1){
            SVNRepository repository = createRepository(url);
            try {
                @SuppressWarnings("unchecked")
				Collection<SVNDirEntry> list = repository.getDir("", -1, null, (List<SVNDirEntry>)null);
                Iterator<SVNDirEntry> iterator = list.iterator();
                while(iterator.hasNext()){
                	SVNDirEntry dirEntry = iterator.next();
                	if(dirEntry.getKind() == SVNNodeKind.DIR){
                		listFolder(dirEntry.getURL().toString());
                	}
                	if(dirEntry.getName().endsWith(".table")){
                		dirs.add(dirEntry);
                	}
                }
                return dirs;
            } catch (SVNException e) {
            	log.log(Level.WARNING,"listFolder error");
            }
        }
        return null;
    }
    
    public void clearFiles(){
    	dirs.clear();
    }

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	public Object read(String input, ClassLoader loader,
			Class<?>[] classes) {
		XStream xstream = new XStream(new DomDriver());
		xstream.autodetectAnnotations(true);
		XStream.setupDefaultSecurity(xstream);
		xstream.setMode(XStream.NO_REFERENCES);
		xstream.processAnnotations(classes);
		xstream.allowTypes(classes);
		xstream.setClassLoader(loader);
		
		return xstream.fromXML(input);
		
	}
	
	@SuppressWarnings("unchecked")
	public void getVersionList(String url) throws SVNException{
		SVNRepository repository = createRepository(url);
		Collection<SVNLogEntry> logEntries = repository.log(new String[]{"src/main/resources/basetable.table"},null,0,-1,true,true);
		Iterator<SVNLogEntry> iterator = logEntries.iterator();
		while(iterator.hasNext()){
			SVNLogEntry logEntry = iterator.next();
			System.out.println("---------------------------------------------------------");
			System.out.println ("revision: " + logEntry.getRevision() );
			System.out.println( "author: " + logEntry.getAuthor() );
			System.out.println( "date: " + logEntry.getDate() );
			System.out.println( "log message: " + logEntry.getMessage());
			Map<String, SVNLogEntryPath> changedPaths = logEntry.getChangedPaths();
			if(changedPaths.size() > 0){
				System.out.println();
				System.out.println("changed path:");
				Set<String> entrySet = changedPaths.keySet();
				Iterator<String> iterator2 = entrySet.iterator();
				while(iterator2.hasNext()){
					SVNLogEntryPath entryPath = (SVNLogEntryPath) changedPaths.get(iterator2.next());
					if(entryPath.getPath().endsWith(".table")){
						System.out.println(" "+entryPath.getType()+" "
											+entryPath.getPath());
					}
				}
			}
		}
	}
	
	public void getFileByVersion(String url,String version) throws SVNException{
		SVNRepository repository = createRepository(url);
		
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        SVNProperties properties = new SVNProperties();
        System.out.println("版本号："+SVNRevision.parse(version).getNumber());
        repository.getFile("", SVNRevision.parse(version).getNumber(), properties, outputStream);
        String doc = new String(outputStream.toByteArray(), Charset.forName("utf-8"));
        System.out.println(doc);
	}
	
	public static void get(SVNUtil svnUtil,String url){
		//获取指定文件内容
				log.log(Level.INFO, "======开始获取文件======");
				String xml = "";
				try {
					xml = svnUtil.checkoutFileToString(url);
				} catch (SVNException e) {
					e.printStackTrace();
				}
				System.out.println(xml);
				
				//转化成实体类模型对象
				Tables tables = (Tables) svnUtil.read(xml, Tables.class.getClassLoader(),
						new Class[] { Tables.class, Table.class, TableField.class, Index.class, ForeignReference.class });
				for(int i=0;i<tables.getTableList().size();i++){
					System.out.println("第"+(i+1)+"张表："+tables.getTableList().get(i)
							.getTitle());
				}
				
				log.log(Level.INFO, "======获取文件结束======");
	}
	
	public static void getFileList(SVNUtil svnUtil,String url){
		svnUtil.clearFiles();
		log.log(Level.INFO, "获取文件开始------------------start");
		long start = System.currentTimeMillis();
		List<SVNDirEntry> dirs = svnUtil.listFolder(url);
		log.log(Level.INFO, "用时："+String.valueOf(System.currentTimeMillis() - start));
		if(null != dirs && dirs.size() > 0){
			for(int i=0;i<dirs.size();i++){
				System.out.println(dirs.get(i));
				dirs.get(i).getKind();
				if(dirs.get(i).getKind() == SVNNodeKind.DIR ){
					System.out.println("目录："+dirs.get(i).getName()+"路径："+dirs.get(i).getURL());
				}
			}
		}
	}

	public static void main(String[] args) throws SVNException {
		String url1 = "https://192.168.57.56/bank/depthree/gapsv5/trunk/Sources/temp/gaps.demo/src/main/resources/basetable.table";
		String url2 = "https://192.168.57.56/bank/depthree/gapsv5/trunk/Sources/temp/gaps.demo";
		SVNUtil svnUtil = new SVNUtil("username", "password");
		
		//get(svnUtil,url);
		
		//getFileList(svnUtil, url);
		
		svnUtil.getVersionList(url2);
		
		svnUtil.getFileByVersion(url1, "852");
		
		DefaultSVNOptions options = new DefaultSVNOptions();
		options.setAuthStorageEnabled(false);
		svnUtil.getUserInfo(options);
	}
	

}
