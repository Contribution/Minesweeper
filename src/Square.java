import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;

public class Square {
	private int number;
	private JButton button;
	private URL imagePath;
	private static final int BOMB = 9;

	Square() {
		number = 0;
		imagePath = imageURL("/unclicked.png");
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public URL getImagePath() {
		return imagePath;
	}

	public void setImage(URL url) {
		this.imagePath = url;
		ImageIcon ic = new ImageIcon(url);
		this.getButton().setIcon(ic);
	}

	public void revealImage() {
		switch (this.getNumber()) {
		case 0:
			this.setImage(imageURL("/empty.png"));
			break;
		case 1:
			this.setImage(imageURL("/one.png"));
			break;
		case 2:
			this.setImage(imageURL("/two.png"));
			break;
		case 3:
			this.setImage(imageURL("/three.png"));
			break;
		case 4:
			this.setImage(imageURL("/four.png"));
			break;
		case 5:
			this.setImage(imageURL("/five.png"));
			break;
		case 6:
			this.setImage(imageURL("/six.png"));
			break;
		case 7: 
			this.setImage(imageURL("/seven.png"));
			break;
		case 8:
			this.setImage(imageURL("/eight.png"));
			break;
		case BOMB:
			this.setImage(imageURL("/bomb.png"));
		}
	}

	public JButton getButton() {
		return button;
	}

	public void setButton(JButton button) {
		this.button = button;
		this.button.setBorder(null);
	}
	
	private URL imageURL(String image) {
		return getClass().getResource(image);
	}

}
