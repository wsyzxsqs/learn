package Basics;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
//import org.junit.Test;
/**
 * 爬取图片，并将图片下载到本地
 * @author vf
 */
public class DemoDownLoadPicture {
    String ALL_URL_STR = "";//保存url地址
    String ALL_SRC_STR = "";//保存SRC地址
    int nonameId = 1;
    int record = 0;
    int noPicname = 0;
    /**
     * 开始，入口
     * 点击strat方法，右键选择run as->JUnit Test 运行程序
     * 
     * 当前获取图片的网站为：http://huaban.com  可更改
     * 存放图片的地址为 ： E://crawler//pic    若无请先创建
     */
    //@Test
    public void start(){
        //要获取的网站地址
        String urlStr = "http://huaban.com";
        String html = getHTML(urlStr);
        getURL(html, 0, "E://crawler//pic");//存放图片的地址；若无请先创建
    }

    /**
     * 获取html网页内容
     * @param urlStr
     * @return
     */
    public String getHTML(String urlStr){
        StringBuilder html = new StringBuilder();
        BufferedReader buffer = null;
        try{
            //将字符串解析为url地址
            URL url = new URL(urlStr);
            //初始化一个连接到那个url的连接
            URLConnection conn = url.openConnection();
            //开始连接
            conn.connect();
            //初始化bufferReader输入流来读取URL的响应
            buffer = new BufferedReader(new InputStreamReader(conn.getInputStream(), Charset.forName("utf-8")));
            String line;
            while((line=buffer.readLine())!=null){
                html.append(line);
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            if(buffer!=null){
                try {
                    buffer.close();
                } catch (IOException e) {
                    throw new RuntimeException("关闭流发生错误");
                }
            }
        }
        return html.toString();
    }

    /**
     * 获取html中url连接
     * @param html
     * @return
     */
    public void getURL(String html, int tmp, String fileName){
        if(tmp>5 || html==null || html.length()==0){
            System.out.println("--------------------");
            System.out.println("----------END-------");
            return ;
        }
        //设置最大下载图片数
        if(record>1000){
            System.out.println("--------------------");
            System.out.println("----------this is biggest-------");
            return ;
        }
        System.out.println("--------------------");
        System.out.println("----------START-------");

        String urlMain = "http://huaban.com";
        String urlPicMain = "http:";
        //解析网页内容
        Document doc = Jsoup.parse(html);
        //获取图片的连接,并下载图片
        Elements imglinks = doc.select("img[src]");
        int picnum = 0;
        String dirFileName = "";
        for(Element imgLink : imglinks ){
            String src = imgLink.attr("src");
            if(src == null || "".equals(src) || src.length()<3){
                continue;
            }
            if(!ALL_SRC_STR.contains(src)){
                ALL_SRC_STR += src + " ##　";
                if(!src.contains(urlPicMain)){
                    src = urlPicMain+src;
                }
                if(picnum==0){
                    //创建新的目录
                    dirFileName = makedir(fileName);
                    picnum++;
                }
                record++;
                downLoadPicture(src, dirFileName);
            }
        }
        //获取所有的a标签
        Elements links = doc.select("a");
        for(Element link : links ){
            String href = link.attr("href");
            String text = link.text();
            if(href == null || "".equals(href) || href.length()<3){
                continue;
            }
            if(text==null || "".equals(text)){
                text = "noName"+nonameId++;
            }
            if(!href.contains(urlMain)){
                href = urlMain+href;
            }
            //去重
            if(!ALL_URL_STR.contains(href)){
                ALL_URL_STR += href + "  ## ";
                System.out.println("******************");
                System.out.println("获取到了新的url地址："+text+"-->"+href);
                getURL(getHTML(href), tmp++ , text);
            }
        }
        return ;
    }

    /**
     * 下载图片到本地
     * @param src
     */
    public void downLoadPicture(String src, String fileName){

        InputStream is = null;
        OutputStream os = null;
        try{
            String imageName = src.substring(src.lastIndexOf("/") + 1,src.length());
            int index = src.lastIndexOf(".");
            String imgType = ".png";
            System.out.println(index);
            if(index!=-1){
                imgType = src.substring(index+1, src.length());
                if(imgType.length()>5){
                    imgType = ".png";
                }
            }
            if(imageName==null || imageName.length()==0){
                imageName = ""+noPicname++ ;
            }
            imageName += imgType;
            //连接url
            URL url = new URL(src);
            URLConnection uri=url.openConnection();
            //获取数据流
            is=uri.getInputStream();
            //写入数据流
           os = new FileOutputStream(new File(fileName, imageName)); 
            byte[] buf = new byte[1024]; 
            int length=0; 
            while ((length = is.read(buf, 0, buf.length)) != -1) {  
                os.write(buf, 0, length); 
            } 
            os.close();
            is.close();
            System.out.println(src+"下载成功！-----");
            }catch(Exception e) {
                 System.out.println(src+"下载失败！-----");
            }finally{
                try{
                    if(os!=null){
                        os.close();
                    }
                    if(is!=null){
                        is.close();
                    }
                }catch (IOException e) {
                    System.out.println("------关闭流发生异常-----");
                }
            }
    }

    /**
     * 创建文件夹（若无则创建，否则做处理）
     * @param filesName
     * @return 创建成功返回创建的文件夹地址；否则返回初始文件地址
     */
    public String makedir(String filesName){
        //定义文件夹路径
        String filePath = "E://crawler//pic//"+filesName;
        File file = new File(filePath);
        if(!file.exists()&&!file.isDirectory())
        {
            file.mkdirs();  //创建文件夹  注意mkdirs()和mkdir()的区别
            //判断是否创建成功
            if(file.exists()&&file.isDirectory())  //文件夹存在并且是文件夹
            {
                System.out.println(filesName+"文件夹创建成功!");
                return filePath;
            }
            else{
                System.out.println(filesName+"文件创建不成功!");
                return "E://crawler//pic";
            }
        }
        else{
            System.out.println(filesName+"文件已经存在!");
            return filePath;
        }
    }
}