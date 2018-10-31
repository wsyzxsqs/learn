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
 * ��ȡͼƬ������ͼƬ���ص�����
 * @author vf
 */
public class DemoDownLoadPicture {
    String ALL_URL_STR = "";//����url��ַ
    String ALL_SRC_STR = "";//����SRC��ַ
    int nonameId = 1;
    int record = 0;
    int noPicname = 0;
    /**
     * ��ʼ�����
     * ���strat�������Ҽ�ѡ��run as->JUnit Test ���г���
     * 
     * ��ǰ��ȡͼƬ����վΪ��http://huaban.com  �ɸ���
     * ���ͼƬ�ĵ�ַΪ �� E://crawler//pic    �������ȴ���
     */
    //@Test
    public void start(){
        //Ҫ��ȡ����վ��ַ
        String urlStr = "http://huaban.com";
        String html = getHTML(urlStr);
        getURL(html, 0, "E://crawler//pic");//���ͼƬ�ĵ�ַ���������ȴ���
    }

    /**
     * ��ȡhtml��ҳ����
     * @param urlStr
     * @return
     */
    public String getHTML(String urlStr){
        StringBuilder html = new StringBuilder();
        BufferedReader buffer = null;
        try{
            //���ַ�������Ϊurl��ַ
            URL url = new URL(urlStr);
            //��ʼ��һ�����ӵ��Ǹ�url������
            URLConnection conn = url.openConnection();
            //��ʼ����
            conn.connect();
            //��ʼ��bufferReader����������ȡURL����Ӧ
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
                    throw new RuntimeException("�ر�����������");
                }
            }
        }
        return html.toString();
    }

    /**
     * ��ȡhtml��url����
     * @param html
     * @return
     */
    public void getURL(String html, int tmp, String fileName){
        if(tmp>5 || html==null || html.length()==0){
            System.out.println("--------------------");
            System.out.println("----------END-------");
            return ;
        }
        //�����������ͼƬ��
        if(record>1000){
            System.out.println("--------------------");
            System.out.println("----------this is biggest-------");
            return ;
        }
        System.out.println("--------------------");
        System.out.println("----------START-------");

        String urlMain = "http://huaban.com";
        String urlPicMain = "http:";
        //������ҳ����
        Document doc = Jsoup.parse(html);
        //��ȡͼƬ������,������ͼƬ
        Elements imglinks = doc.select("img[src]");
        int picnum = 0;
        String dirFileName = "";
        for(Element imgLink : imglinks ){
            String src = imgLink.attr("src");
            if(src == null || "".equals(src) || src.length()<3){
                continue;
            }
            if(!ALL_SRC_STR.contains(src)){
                ALL_SRC_STR += src + " ##��";
                if(!src.contains(urlPicMain)){
                    src = urlPicMain+src;
                }
                if(picnum==0){
                    //�����µ�Ŀ¼
                    dirFileName = makedir(fileName);
                    picnum++;
                }
                record++;
                downLoadPicture(src, dirFileName);
            }
        }
        //��ȡ���е�a��ǩ
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
            //ȥ��
            if(!ALL_URL_STR.contains(href)){
                ALL_URL_STR += href + "  ## ";
                System.out.println("******************");
                System.out.println("��ȡ�����µ�url��ַ��"+text+"-->"+href);
                getURL(getHTML(href), tmp++ , text);
            }
        }
        return ;
    }

    /**
     * ����ͼƬ������
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
            //����url
            URL url = new URL(src);
            URLConnection uri=url.openConnection();
            //��ȡ������
            is=uri.getInputStream();
            //д��������
           os = new FileOutputStream(new File(fileName, imageName)); 
            byte[] buf = new byte[1024]; 
            int length=0; 
            while ((length = is.read(buf, 0, buf.length)) != -1) {  
                os.write(buf, 0, length); 
            } 
            os.close();
            is.close();
            System.out.println(src+"���سɹ���-----");
            }catch(Exception e) {
                 System.out.println(src+"����ʧ�ܣ�-----");
            }finally{
                try{
                    if(os!=null){
                        os.close();
                    }
                    if(is!=null){
                        is.close();
                    }
                }catch (IOException e) {
                    System.out.println("------�ر��������쳣-----");
                }
            }
    }

    /**
     * �����ļ��У������򴴽�������������
     * @param filesName
     * @return �����ɹ����ش������ļ��е�ַ�����򷵻س�ʼ�ļ���ַ
     */
    public String makedir(String filesName){
        //�����ļ���·��
        String filePath = "E://crawler//pic//"+filesName;
        File file = new File(filePath);
        if(!file.exists()&&!file.isDirectory())
        {
            file.mkdirs();  //�����ļ���  ע��mkdirs()��mkdir()������
            //�ж��Ƿ񴴽��ɹ�
            if(file.exists()&&file.isDirectory())  //�ļ��д��ڲ������ļ���
            {
                System.out.println(filesName+"�ļ��д����ɹ�!");
                return filePath;
            }
            else{
                System.out.println(filesName+"�ļ��������ɹ�!");
                return "E://crawler//pic";
            }
        }
        else{
            System.out.println(filesName+"�ļ��Ѿ�����!");
            return filePath;
        }
    }
}