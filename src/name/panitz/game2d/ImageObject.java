package name.panitz.game2d;
import java.awt.*;
import javax.swing.ImageIcon;
public record ImageObject(String name, Vertex pos, Vertex velocity, Vertex anchor
                  , double width, double height
                  , String fileName, Image image)
    implements GameObj{

  public ImageObject{
    var iIcon 
     = new ImageIcon(getClass().getClassLoader().getResource(fileName));
    width = iIcon.getIconWidth();
    height=iIcon.getIconHeight();
    image = iIcon.getImage();
  }

  public ImageObject(Vertex pos,Vertex anchor,Vertex velocity, String fileName){
    this("", pos,velocity,anchor,0,0,fileName,null);
  }

  public ImageObject(String fileName){
    this(new Vertex(0,0),new Vertex(0,0),new Vertex(0,0),fileName);
  }

  @Override
  public void Update() {

  }

  public void paintTo(Graphics g){
    g.drawImage(image, (int)pos.x, (int)pos.y, null);
  }
}

