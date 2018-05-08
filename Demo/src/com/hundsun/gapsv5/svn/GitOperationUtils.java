package com.hundsun.gapsv5.svn;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.lib.MutableObjectId;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTag;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.TreeWalk;

public class GitOperationUtils {
	
	public GitOperationUtils(){
		
	}
	
	public Repository openJGitCookBookRepository() throws IOException{
		
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		
		Repository build = builder.setGitDir(new File(System.getProperty("user.dir"))).readEnvironment().findGitDir().build();
		
		return build;
	}
	
	
	public Repository createNewRepository() throws IOException{
		File tempFile = File.createTempFile("TestGitRespoitory", "");
		if(!tempFile.delete()){
			throw new IOException("could not delete "+tempFile);
		}
		
		Repository repository = FileRepositoryBuilder.create(new File(tempFile,".git"));
		repository.create();
		return repository;
	}
	
	/**
	 * 获取日志信息
	 * @throws IOException
	 * @throws NoHeadException
	 * @throws GitAPIException
	 */
	public void getLogInfo() throws IOException, NoHeadException, GitAPIException{
		Git git = Git.open(new File(openJGitCookBookRepository().getDirectory().getParent()+"/.git"));
		Iterable<RevCommit> gitLog = git.log().call();
		for(RevCommit commit : gitLog){
			String version = commit.getName();
			String name = commit.getCommitterIdent().getName();
			String emailAddress = commit.getCommitterIdent().getEmailAddress();
			Date when = commit.getCommitterIdent().getWhen();
			String fullMessage = commit.getFullMessage();
			System.out.println(version+"\t"+name+"\t"+emailAddress+"\t"+formatDate(when, "yyyy年MM月dd日 HH:mm:SS")+"\t"+fullMessage);
		}
	}
	
	/**
	 * 获取指定文件的日志信息
	 * @param path
	 * @throws IOException
	 * @throws NoHeadException
	 * @throws GitAPIException
	 */
	public void getSpecifiedFileVersionInfo(String path) throws IOException, NoHeadException, GitAPIException{
		Git git = Git.open(new File("C:/Users/Administrator/git/GapsDemo/.git"));
		Iterable<RevCommit> gitLog = git.log().addPath(path).call();
		for(RevCommit commit : gitLog){
			String version = commit.getName();
			String name = commit.getCommitterIdent().getName();
			String emailAddress = commit.getCommitterIdent().getEmailAddress();
			Date when = commit.getCommitterIdent().getWhen();
			String fullMessage = commit.getFullMessage();
			System.out.println(version+"\t"+name+"\t"+emailAddress+"\t"+formatDate(when, "yyyy年MM月dd日 HH:mm:SS")+"\t"+fullMessage);
		}
	}
	
	/**
	 * 根据指定版本号获取文件内容
	 * @param version
	 * @return
	 * @throws IOException
	 */
	public String getSpecifiedVersionFile(String version) throws IOException{
		//default repository folder
		Git git = Git.open(new File("C:/Users/Administrator/git/GapsDemo/.git"));
		Repository repository = git.getRepository();
		RevWalk revWalk = new RevWalk(repository);
		
		ObjectId objectId = repository.resolve(version);
		RevCommit parseCommit = revWalk.parseCommit(objectId);
		RevTree tree = parseCommit.getTree();
		
		String path = "gaps.demo/src/main/resources/metadatas/basetable.table";
		TreeWalk treeWalk = TreeWalk.forPath(repository, path, tree);
		ObjectId blobId = treeWalk.getObjectId(0);
		ObjectLoader open = repository.open(blobId);
		revWalk.close();
		return new String(open.getBytes(),"UTF-8");
	}
	
	/**
	 * 获取所有的版本文件内容
	 * @param parseCommit
	 * @return
	 * @throws IOException
	 */
	public String getAllVersionFileContent(RevCommit parseCommit) throws IOException{
		Git git = Git.open(new File("C:/Users/Administrator/git/GapsDemo/.git"));
		Repository repository = git.getRepository();
		String str = "";
		TreeWalk treeWalk = new TreeWalk(repository);
		treeWalk.addTree(parseCommit.getTree());
		treeWalk.setRecursive(true);
		MutableObjectId id = new MutableObjectId();
		while(treeWalk.next()){
			 treeWalk.getObjectId(id, 0);
			 ObjectId objectId = treeWalk.getObjectId(0);
			 ObjectLoader open = repository.open(objectId);
			 byte[] bytes = open.getBytes();
			 str += new String(bytes, "UTF-8");
		}
		treeWalk.close();
		return str;
	}
	
	/**
	 * 获取标签信息
	 * @param version
	 * @throws IOException
	 */
	public void getRevTagInfo(String version) throws IOException{
		Git git = Git.open(new File(openJGitCookBookRepository().getDirectory().getParent()+"/.git"));
		Repository repository = git.getRepository();
		Map<String, Ref> tags = repository.getTags();
		RevWalk revWalk = new RevWalk(repository);
		Iterator<Entry<String, Ref>> iterator = tags.entrySet().iterator();
		while(iterator.hasNext()){
			Entry<String, Ref> next = iterator.next();
			Ref ref = next.getValue();
			ObjectId objectId = ref.getObjectId();
			RevTag parseTag = revWalk.parseTag(objectId);
			System.out.println(parseTag.getId());
			System.out.println(parseTag.getName());
			System.out.println(parseTag.getTagName());
			System.out.println(parseTag.getFullMessage());
			System.out.println(parseTag.getShortMessage());
		}
		revWalk.close();
	}
	
	public String formatDate(Date date,String pattern){
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		return format.format(date);
	}
	
	
	
	public static void main(String[] args){
		GitOperationUtils utils = new GitOperationUtils();
		try {
			System.out.println(utils.openJGitCookBookRepository().getDirectory().getParent());
			System.out.println("=================================================================");
			//String absolutePath = utils.createNewRepository().getDirectory().getAbsolutePath();
			//System.out.println(absolutePath);
			
			utils.getLogInfo();
			System.out.println("=================================================================");
			//System.out.println(utils.getSpecifiedVersionFile("f07afa28aa7c5f534b69f32fda9434737783dd8c"));
			//utils.getSpecifiedFileVersionInfo("gaps.demo/src/main/resources/metadatas/basetable.table");
			utils.getRevTagInfo("c8c7669ec9b251cf3506ba08b50d4f555ce3e877");
			System.out.println("=================================================================");
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NoHeadException e) {
			e.printStackTrace();
		} catch (GitAPIException e) {
			e.printStackTrace();
		}
		
	}

}
