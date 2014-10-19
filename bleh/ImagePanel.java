import javax.swing.JPanel;
import java.awt.Image;
import java.awt.Graphics;

//a panel that contains an image
public class ImagePanel extends JPanel{

	//The image that this panel draws
	Image myImage;

	public ImagePanel(Image me)
	{
		myImage = me;

		//Makes the panel the same size as the image
		setPreferredSize(new java.awt.Dimension(me.getWidth(null),
												me.getHeight(null)));
	}

	//override the paint method to draw the image on the panel
	public void paint(Graphics g)
	{
		g.drawImage(myImage, 0, 0, null);
	}
}
