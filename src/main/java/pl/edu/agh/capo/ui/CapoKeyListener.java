package pl.edu.agh.capo.ui;

import pl.edu.agh.capo.logic.common.AgentMove;
import pl.edu.agh.capo.logic.interfaces.IAgentMoveListener;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class CapoKeyListener implements KeyListener {

    private IAgentMoveListener agentMoveListener;

    public CapoKeyListener(IAgentMoveListener agentMoveListener){
        super();
        this.agentMoveListener = agentMoveListener;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        agentMoveListener.onAgentMoved(AgentMove.getValue(e.getKeyChar()));
    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
