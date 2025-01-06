import MyMath.MPoint3d;
import MyMath.MVector3D;
import MyMath.Model;
import track.Checkpoint;
import track.Road;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
//TODO speedometer rpm meter and time meter

public class GameFrame extends JFrame {
    GamePanel gamePanel;
    boolean left;
    boolean right;
    boolean up;
    boolean down;
    boolean shift;
    boolean ekey;
    boolean qkey;
    boolean pkey;
    boolean tapkey;
    boolean enterkey;
    GameFrame() {
        gamePanel = new GamePanel();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("Game");
        this.add(gamePanel);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setResizable(true);
        this.pack();
        this.setVisible(true);
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    left = true;
                }
                if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    right = true;
                }
                if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    down = true;
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    up = true;
                }
                if(e.getKeyCode() == KeyEvent.VK_SHIFT) {
                    shift = true;
                }
                if(e.getKeyCode() == KeyEvent.VK_E) {
                    ekey = true;
                }
                if(e.getKeyCode() == KeyEvent.VK_Q) {
                    qkey = true;
                }
                if(e.getKeyCode() == KeyEvent.VK_P) {
                    pkey = true;
                }
                if (e.getKeyCode() == KeyEvent.VK_Z) {
                    tapkey=true;
                }
                if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                    enterkey=true;
                }

            }
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    left = false;
                }
                if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    right = false;
                }
                if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    down = false;
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    up = false;
                }
                if(e.getKeyCode() == KeyEvent.VK_SHIFT) {
                    shift = false;
                }
                if(e.getKeyCode() == KeyEvent.VK_E) {
                    ekey = false;
                }
                if(e.getKeyCode() == KeyEvent.VK_Q) {
                    qkey = false;
                }
                if(e.getKeyCode() == KeyEvent.VK_P) {
                    pkey = false;
                }
                if (e.getKeyCode() == KeyEvent.VK_Z) {
                    tapkey=false;
                }
                if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                    enterkey=false;
                }
            }
        });
    }
    public boolean isLeftPressed() {
        return left;
    }

    public boolean isRightPressed() {
        return right;
    }

    public boolean isUpPressed() {
        return up;
    }
    public boolean isTapkeyPressed() {
        return tapkey;
    }

    public boolean isDownPressed() {
        return down;
    }
    public boolean isShiftPressed() {
        return shift;
    }
    public boolean isEkeyPressed() {
        return ekey;
    }
    public boolean isQkeyPressed() {
        return qkey;
    }
    public boolean isEnterkeyPressed() {return enterkey; }

    public void render(Model model, MPoint3d light, float h, MPoint3d c,MPoint3d facingpoint,MPoint3d top) {
        gamePanel.invalidate();
        gamePanel.updateScene(model, light, h, c,facingpoint,top);
        gamePanel.validate();
        gamePanel.setVec(false);
    }
    public void renderv(Model model, MPoint3d light, float h, MPoint3d c, MVector3D facing, MPoint3d top) {
        gamePanel.invalidate();
        gamePanel.updateScene(model, light, h, c,facing,top);
        gamePanel.validate();
        gamePanel.setVec(true);
    }
    public void setText(String text){
        gamePanel.setString(text);
    }
    public void setCheckpints(Checkpoint[] checkpoints){
        gamePanel.setcheckpoints(checkpoints);
    }
    public void setRoads(Road[] roads){gamePanel.setRoads(roads);}
    public void endScreen(double time){
        gamePanel.endScreen(time);
    }
}
