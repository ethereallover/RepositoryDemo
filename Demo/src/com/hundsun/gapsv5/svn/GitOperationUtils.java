package com.hundsun.gapsv5.svn;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.diff.EditList;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.lib.MutableObjectId;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.patch.FileHeader;
import org.eclipse.jgit.patch.HunkHeader;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTag;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.TreeWalk;

import com.hundsun.gapsv5.svn.bean.GitLogVersion;

public class GitOperationUtils {
	
	private List<GitLogVersion> lists = new ArrayList<GitLogVersion>();
	
	private Git git;
	
	private Repository repository;
	
	public GitOperationUtils(){
	}
	
	public void initRepository() throws IOException{
		git = Git.open(new File(openJGitCookBookRepository().getDirectory().getParent()+"/.git"));
		repository = git.getRepository();
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
		Iterable<RevCommit> gitLog = git.log().call();
		for(RevCommit commit : gitLog){
			String version = commit.getName();
			String name = commit.getCommitterIdent().getName();
			String emailAddress = commit.getCommitterIdent().getEmailAddress();
			Date when = commit.getCommitterIdent().getWhen();
			String fullMessage = commit.getFullMessage().replaceAll("\r|\n|\t", "");;
			System.out.println(version+"\t"+name+"\t"+emailAddress+"\t"+formatDate(when, "yyyy年MM月dd日 HH:mm:ss")+"\t"+fullMessage);
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
		Iterable<RevCommit> gitLog = git.log().addPath(path).call();
		for(RevCommit commit : gitLog){
			String version = commit.getName();
			String name = commit.getCommitterIdent().getName();
			String emailAddress = commit.getCommitterIdent().getEmailAddress();
			Date when = commit.getCommitterIdent().getWhen();
			String fullMessage = commit.getFullMessage().replaceAll("\r|\n|\t", "");
			System.out.println(version+"\t"+name+"\t"+emailAddress+"\t"+formatDate(when, "yyyy年MM月dd日 HH:mm:ss")+"\t"+fullMessage);
			
			GitLogVersion logVersion = new GitLogVersion();
			logVersion.setId(version.substring(0, 7));
			logVersion.setAuthor(commit.getAuthorIdent().getName());
			logVersion.setAuthoredDate(formatDate(commit.getAuthorIdent().getWhen(), "yyyy年MM月dd日 HH:mm:ss"));
			logVersion.setCommitter(name);
			logVersion.setCommittedDate(formatDate(when, "yyyy年MM月dd日 HH:mm:ss"));
			logVersion.setMessage(fullMessage);
			lists.add(logVersion);
		}
	}
	
	/**
	 * 根据指定版本号获取文件内容
	 * @param version
	 * @return
	 * @throws IOException
	 */
	public String getSpecifiedVersionFile(String version,String path) throws IOException{
		//default repository folder
		RevWalk revWalk = new RevWalk(repository);
		
		ObjectId objectId = repository.resolve(version);
		RevCommit parseCommit = revWalk.parseCommit(objectId);
		RevTree tree = parseCommit.getTree();
		
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
	 *  获取标签信息(只针对含附注的标签)
	 * @return 所有的含附注的标签集合
	 * @throws IOException
	 */
	public List<RevTag> getRevTags() throws IOException{
		List<RevTag> tags = new ArrayList<RevTag>();
		repository = git.getRepository();
		Map<String, Ref> tagMap = repository.getTags();
		RevWalk revWalk = new RevWalk(repository);
		Iterator<Entry<String, Ref>> iterator = tagMap.entrySet().iterator();
		while(iterator.hasNext()){
			Entry<String, Ref> next = iterator.next();
			Ref ref = next.getValue();
			ObjectId objectId = ref.getObjectId();
			RevTag parseTag = revWalk.parseTag(objectId);
			tags.add(parseTag);
		}
		revWalk.close();
		return tags;
	}
	
	/**
	 * 获取指定标签版本号(TagName)的标签对象
	 * @param version
	 * @return 指定TagName的标签对象
	 * @throws IOException
	 */
	public RevTag getTagInfo(String version) throws IOException{
		Map<String, Ref> tagMap = repository.getTags();
		RevWalk revWalk = new RevWalk(repository);
		Ref ref = tagMap.get(version);
		ObjectId objectId = ref.getObjectId();
		RevTag parseTag = revWalk.parseTag(objectId);
		
		revWalk.close();
		return parseTag;
	}
	
	/**
	 * 根据指定路径获取标签号下的文件内容
	 * @param filePath
	 * @param objectId
	 * @return  指定文件的内容字符串
	 * @throws IOException
	 */
	public String getTagFileContent(String filePath,ObjectId objectId) throws IOException{
		RevWalk revWalk = new RevWalk(repository);
		RevCommit parseCommit = revWalk.parseCommit(objectId);
		RevTree tree = parseCommit.getTree();
		TreeWalk treeWalk = TreeWalk.forPath(repository, filePath, tree);
		ObjectId blobId = treeWalk.getObjectId(0);
		ObjectLoader open = repository.open(blobId);
		
		revWalk.close();
		return new String(open.getBytes(),"UTF-8");
	}
	
	/**
	 * 获取指定文件的最近两次提交对比结果
	 * @param path
	 * @throws IOException
	 * @throws NoHeadException
	 * @throws GitAPIException
	 */
	@SuppressWarnings("unchecked")
	public void getDiffContent(String path) throws IOException, NoHeadException, GitAPIException{
        List<RevCommit> commitList = new ArrayList<>();  
        //获取最近提交的两次记录  
        Iterable<RevCommit> commits = git.log().addPath(path).setMaxCount(2).call();
        for(RevCommit commit:commits){ 
        	commitList.add(commit);
        }
        if(commitList.size() == 2){
        	AbstractTreeIterator newTreeParser = prepareTreeParser(commitList.get(0), repository);
        	AbstractTreeIterator oldTreeParser = prepareTreeParser(commitList.get(1), repository);
        	List<DiffEntry> diffs = git.diff().setOldTree(oldTreeParser).setNewTree(newTreeParser).setShowNameAndStatusOnly(true).call();
        	
        	ByteArrayOutputStream out = new ByteArrayOutputStream();
        	DiffFormatter formatter = new DiffFormatter(out);
        	 //设置比较器为忽略空白字符对比(Ignores all whitespace)
        	formatter.setDiffComparator(RawTextComparator.WS_IGNORE_ALL);  
        	formatter.setRepository(git.getRepository()); 
        	
        	for(DiffEntry diffEntry : diffs){
        		formatter.format(diffEntry);
        		String diffText = out.toString("UTF-8");
        		System.out.println(diffText);
        		
        		//获取文件差异位置，从而统计差异的行数，如增加行数，减少行数  
                FileHeader fileHeader = formatter.toFileHeader(diffEntry);  
                List<HunkHeader> hunks = (List<HunkHeader>) fileHeader.getHunks();  
                int addSize = 0;  
                int subSize = 0;  
                for(HunkHeader hunkHeader:hunks){  
                    EditList editList = hunkHeader.toEditList();  
                    for(Edit edit : editList){  
                        subSize += edit.getEndA()-edit.getBeginA();  
                        addSize += edit.getEndB()-edit.getBeginB();  
                    }  
                }  
                System.out.println("addSize="+addSize);  
                System.out.println("subSize="+subSize);  
        	}
        	formatter.close();
        	out.reset();
        }
		
	}
	
	/**
	 * 获取解析树
	 * @param commit
	 * @param repository
	 * @return
	 */
	public AbstractTreeIterator prepareTreeParser(RevCommit commit,Repository repository){
		try (RevWalk walk = new RevWalk(repository)) {
			RevTree tree = walk.parseTree(commit.getTree().getId());
			CanonicalTreeParser treeParser = new CanonicalTreeParser();
			ObjectReader reader = repository.newObjectReader();
			treeParser.reset(reader, tree.getId());
			walk.dispose();
			return treeParser;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String formatDate(Date date,String pattern){
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		return format.format(date);
	}
	
	public List<GitLogVersion> getLists() {
		return lists;
	}

	public static void main(String[] args){
		GitOperationUtils utils = new GitOperationUtils();
		//String path = "gaps.demo/src/main/resources/metadatas/basetable.table";
		String path = "Demo/src/com/hundsun/gapsv5/svn/GitOperationUtils.java";
		try {
			utils.initRepository();
			
			utils.getLogInfo();
			System.out.println("=================================================================");
			//utils.getSpecifiedFileVersionInfo(path);
			System.out.println(utils.getTagFileContent(path, utils.getTagInfo("v1.0.0").getObject()));
			System.out.println("=================================================================");
			utils.getRevTags();
			RevTag parseTag = utils.getTagInfo("v1.0.0");
			System.out.println(parseTag.getTagName()+"\t"+parseTag.getTaggerIdent().getName()
					+"\t"+parseTag.getTaggerIdent().getEmailAddress()
					+"\t"+utils.formatDate(parseTag.getTaggerIdent().getWhen(), "yyyy年MM月dd日  HH:mm:ss")
					+"\t"+parseTag.getFullMessage().replaceAll("\r|\n|\t", ""));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NoHeadException e) {
			e.printStackTrace();
		} catch (GitAPIException e) {
			e.printStackTrace();
		}
		
	}

}
