package Controller.Parse;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;

/**
 * Created by SunnyD on 2017/6/23.
 */
public class SVGModelParser {
    private final SAXReader reader;
    private final BufferedImageTranscoder trans;
    private HashMap<String,ImageView> funcImagesMap;
    private HashMap<String,ImageView> eventImagesMap;
    private Element baseElement;
    private String svgStr;

    public SVGModelParser(){
        reader = new SAXReader();
        trans = new BufferedImageTranscoder();
        funcImagesMap = new HashMap<>();
        eventImagesMap = new HashMap<>();
    }
    public void read(String svgStr){
        this.svgStr=svgStr;
        Stack<Element> sta=new Stack<>();
        try {
            Document doc = this.reader.read(new ByteArrayInputStream(svgStr.getBytes("UTF-8")));
            Element firstG=doc.getRootElement().element("g");
            Iterator<Element> it = firstG.elementIterator();
            while(it.hasNext()){
                Element e = it.next();
                if (e.getName().equals("g")){
                    Element p=sta.pop();//获取轮廓节点
                    Document pDoc= this.createBaseDocument(this.svgStr);
                    Element pFirst=pDoc.getRootElement().element("g");
                    pFirst.add((Element) p.clone());//添加轮廓节点
                    pFirst.add((Element) e.clone());//添加文字节点

                    ImageView pImage=this.createImageView(this.generateXMLStr(pDoc));
                    //funcNode
                    if(p.attributeValue("fill").equals("#80ff80")){
                        funcImagesMap.put(e.element("text").getText(),pImage);
                    }else if(p.attributeValue("file").equals("#ff8080")){
                        eventImagesMap.put(e.element("text").getText(),pImage);
                    }



                }else{
                    sta.push(e);
                }
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }catch (DocumentException e){
            e.printStackTrace();
        }
    }

    private Document createBaseDocument(String svgStr){
        Document doc=null;
        try {
            doc = this.reader.read(new ByteArrayInputStream(svgStr.getBytes("UTF-8")));
            doc.getRootElement().element("g").elements().clear();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }catch (DocumentException e){
            e.printStackTrace();
        }
        return doc;
    }

    private ImageView createImageView(String svgStr){
        TranscoderInput transIn = new TranscoderInput(new ByteArrayInputStream(svgStr.getBytes()));
        ImageView imageView =new ImageView();
        try {
            trans.transcode(transIn, null);
            Image img = SwingFXUtils.toFXImage(trans.getBufferedImage(), null);
            imageView.setImage(img);
        } catch (TranscoderException e) {
            e.printStackTrace();
        }
        return imageView;
    }

    public String generateXMLStr(Document doc){
        OutputFormat format = OutputFormat.createCompactFormat();
        StringWriter writer = new StringWriter();
        XMLWriter xmlWriter = new XMLWriter(writer,format);

        try {
            xmlWriter.write(doc);
            xmlWriter.close();
            writer.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return writer.toString();

    }

    public static void main(String[] args) {
        SVGModelParser p = new SVGModelParser();
        p.read("<svg xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" width=\"623px\" height=\"273px\" version=\"1.1\" style=\"background-color: rgb(255, 255, 255);\">\n" +
                "\t<defs/>\n" +
                "\t<g transform=\"translate(0.5,0.5)\">\n" +
                "\t\t<path d=\"M 101 141 L 121 141 L 111 141 L 124.63 141\" fill=\"none\" stroke=\"#000000\" stroke-miterlimit=\"10\" pointer-events=\"none\"/>\n" +
                "\t\t<path d=\"M 129.88 141 L 122.88 144.5 L 124.63 141 L 122.88 137.5 Z\" fill=\"#000000\" stroke=\"#000000\" stroke-miterlimit=\"10\" pointer-events=\"none\"/>\n" +
                "\t\t<path d=\"M 26 111 L 76 111 L 101 141 L 76 171 L 26 171 L 1 141 Z\" fill=\"#ff8080\" stroke=\"#000000\" stroke-miterlimit=\"10\" pointer-events=\"none\"/>\n" +
                "\t\t<g fill=\"#000000\" font-family=\"Helvetica\" text-anchor=\"middle\" font-size=\"12px\">\n" +
                "\t\t\t<text x=\"50.5\" y=\"144.5\">start</text>\n" +
                "\t\t</g>\n" +
                "\t\t<path d=\"M 231 141 L 251 141 L 241 141 L 254.63 141\" fill=\"none\" stroke=\"#000000\" stroke-miterlimit=\"10\" pointer-events=\"none\"/>\n" +
                "\t\t<path d=\"M 259.88 141 L 252.88 144.5 L 254.63 141 L 252.88 137.5 Z\" fill=\"#000000\" stroke=\"#000000\" stroke-miterlimit=\"10\" pointer-events=\"none\"/>\n" +
                "\t\t<rect x=\"131\" y=\"111\" width=\"100\" height=\"60\" rx=\"9\" ry=\"9\" fill=\"#80ff80\" stroke=\"#000000\" pointer-events=\"none\"/>\n" +
                "\t\t<g fill=\"#000000\" font-family=\"Helvetica\" text-anchor=\"middle\" font-size=\"12px\">\n" +
                "\t\t\t<text x=\"180.5\" y=\"144.5\">addItem</text>\n" +
                "\t\t</g>\n" +
                "\t\t<path d=\"M 361 141 L 381 141 L 371 141 L 384.63 141\" fill=\"none\" stroke=\"#000000\" stroke-miterlimit=\"10\" pointer-events=\"none\"/>\n" +
                "\t\t<path d=\"M 389.88 141 L 382.88 144.5 L 384.63 141 L 382.88 137.5 Z\" fill=\"#000000\" stroke=\"#000000\" stroke-miterlimit=\"10\" pointer-events=\"none\"/>\n" +
                "\t\t<path d=\"M 286 111 L 336 111 L 361 141 L 336 171 L 286 171 L 261 141 Z\" fill=\"#ff8080\" stroke=\"#000000\" stroke-miterlimit=\"10\" pointer-events=\"none\"/>\n" +
                "\t\t<g fill=\"#000000\" font-family=\"Helvetica\" text-anchor=\"middle\" font-size=\"12px\">\n" +
                "\t\t\t<text x=\"310.5\" y=\"144.5\">add1</text>\n" +
                "\t\t</g>\n" +
                "\t\t<path d=\"M 491 141 L 511 141 L 501 141 L 514.63 141\" fill=\"none\" stroke=\"#000000\" stroke-miterlimit=\"10\" pointer-events=\"none\"/>\n" +
                "\t\t<path d=\"M 519.88 141 L 512.88 144.5 L 514.63 141 L 512.88 137.5 Z\" fill=\"#000000\" stroke=\"#000000\" stroke-miterlimit=\"10\" pointer-events=\"none\"/>\n" +
                "\t\t<rect x=\"391\" y=\"111\" width=\"100\" height=\"60\" rx=\"9\" ry=\"9\" fill=\"#80ff80\" stroke=\"#000000\" pointer-events=\"none\"/>\n" +
                "\t\t<g fill=\"#000000\" font-family=\"Helvetica\" text-anchor=\"middle\" font-size=\"12px\">\n" +
                "\t\t\t<text x=\"440.5\" y=\"144.5\">addItem</text>\n" +
                "\t\t</g>\n" +
                "\t\t<path d=\"M 546 111 L 596 111 L 621 141 L 596 171 L 546 171 L 521 141 Z\" fill=\"#ff8080\" stroke=\"#000000\" stroke-miterlimit=\"10\" pointer-events=\"none\"/>\n" +
                "\t\t<g fill=\"#000000\" font-family=\"Helvetica\" text-anchor=\"middle\" font-size=\"12px\">\n" +
                "\t\t\t<text x=\"570.5\" y=\"144.5\">end</text>\n" +
                "\t\t</g>\n" +
                "\t\t<path d=\"M 441 211 L 441 177.37\" fill=\"none\" stroke=\"#000000\" stroke-miterlimit=\"10\" pointer-events=\"none\"/>\n" +
                "\t\t<path d=\"M 441 172.12 L 444.5 179.12 L 441 177.37 L 437.5 179.12 Z\" fill=\"#000000\" stroke=\"#000000\" stroke-miterlimit=\"10\" pointer-events=\"none\"/>\n" +
                "\t\t<rect x=\"391\" y=\"211\" width=\"100\" height=\"60\" fill=\"#0000ff\" stroke=\"#000000\" pointer-events=\"none\"/>\n" +
                "\t\t<g fill=\"#000000\" font-family=\"Helvetica\" text-anchor=\"middle\" font-size=\"12px\">\n" +
                "\t\t\t<text x=\"440.5\" y=\"244.5\">信息/材料</text>\n" +
                "\t\t</g>\n" +
                "\t\t<path d=\"M 441 61 L 441 104.63\" fill=\"none\" stroke=\"#000000\" stroke-miterlimit=\"10\" pointer-events=\"none\"/>\n" +
                "\t\t<path d=\"M 441 109.88 L 437.5 102.88 L 441 104.63 L 444.5 102.88 Z\" fill=\"#000000\" stroke=\"#000000\" stroke-miterlimit=\"10\" pointer-events=\"none\"/>\n" +
                "\t\t<ellipse cx=\"441\" cy=\"31\" rx=\"50\" ry=\"30\" fill=\"#ffff80\" stroke=\"#000000\" pointer-events=\"none\"/>\n" +
                "\t\t<g fill=\"#000000\" font-family=\"Helvetica\" text-anchor=\"middle\" font-size=\"12px\">\n" +
                "\t\t\t<text x=\"440.5\" y=\"34.5\">组织(管理员)</text>\n" +
                "\t\t</g>\n" +
                "\t\t<path d=\"M 411 61 L 411 1\" fill=\"none\" stroke=\"#000000\" stroke-miterlimit=\"10\" pointer-events=\"none\"/>\n" +
                "\t</g>\n" +
                "</svg>");

    }

    public HashMap<String, ImageView> getFuncImagesMap() {
        return funcImagesMap;
    }

    public HashMap<String, ImageView> getEventImagesMap() {
        return eventImagesMap;
    }

}
