package pl.edu.agh.capo.ui;

import pl.edu.agh.capo.logic.common.AgentMove;
import pl.edu.agh.capo.logic.listener.IAgentMoveListener;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class CapoKeyListener implements KeyListener {

    private IAgentMoveListener agentMoveListener;

    public CapoKeyListener(IAgentMoveListener agentMoveListener) {
        super();
        this.agentMoveListener = agentMoveListener;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        AgentMove move = AgentMove.getValue(e.getKeyChar());
        if (move != null) {
            agentMoveListener.onAgentMoved(move);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
