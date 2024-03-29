package ui;

import data.ChatMessage;
import rmi.IServer;
import shape.IShape;
import shape.ShapeType;
import util.Looper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

@SuppressWarnings("ALL")
public abstract class BaseMainFrame extends JFrame {

    protected JPanel boardPanel = new JPanel();
    protected JPanel optionPanel = new JPanel();
    protected JPanel shapeOptionPanel = new JPanel();
    protected JPanel eraserOptionPanel = new JPanel();
    protected JPanel textOpentionPanel = new JPanel();
    protected ChatBoard chatBoard;
    protected PaintBoard paintBoard;
    protected UserListBoard userListBoard;
    protected String username;

    protected Looper looper;

    protected IServer server;

    public BaseMainFrame(String username, String windowName) {
        super(windowName);
        looper = new Looper();
        this.username = username;
        paintBoard = new PaintBoard(username, looper);
        chatBoard = new ChatBoard(username, looper);
        userListBoard = new UserListBoard(username, looper);
        setSize(1000, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        setLayout(new BorderLayout(10, 10));

        initMenuBar();
        initComponents();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                onWindowClosing();
                super.windowClosing(e);
            }
        });
        paintBoard.setMainFrame(this);
    }

    public void setServer(IServer server) {
        this.server = server;
        paintBoard.setServer(server);
        chatBoard.setServer(server);
        userListBoard.setServer(server);
    }

    private void initComponents() {
        JPopupMenu.setDefaultLightWeightPopupEnabled(false);

        chatBoard.setPreferredSize(new Dimension(300, 800));
        userListBoard.setPreferredSize(new Dimension(200, 800));
        add(boardPanel, BorderLayout.CENTER);
        add(chatBoard, BorderLayout.EAST);
        add(userListBoard, BorderLayout.WEST);
        boardPanel.setLayout(new BorderLayout());
//        PaintBoard paintBoard = new PaintBoard();
        String[] optionListData = new String[]{"Line", "Rectangle", "Circle", "Oval", "Free", "Text", "Eraser"};
        JComboBox<String> optionMenu = new JComboBox<>(optionListData);
        boardPanel.add(optionMenu, BorderLayout.NORTH);
        boardPanel.add(paintBoard, BorderLayout.CENTER);
        boardPanel.add(optionPanel, BorderLayout.SOUTH);

        CardLayout cardLayout = new CardLayout();
        optionPanel.setLayout(cardLayout);
        shapeOptionPanel.setLayout(new FlowLayout());

        Integer[] eraserSizeData = new Integer[]{10, 20, 30, 40, 50, 60};
        JComboBox<Integer> eraserSizeBox = new JComboBox<>(eraserSizeData);
        eraserSizeBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                paintBoard.setEraseSize(eraserSizeData[eraserSizeBox.getSelectedIndex()]);
            }
        });
        JButton clearButton = new JButton("Clear");
        clearButton.addActionListener(e -> paintBoard.clearShapes());
        eraserOptionPanel.add(new JLabel("Eraser size: "));
        eraserOptionPanel.add(eraserSizeBox);
        eraserOptionPanel.add(clearButton);

        Integer[] shapeSizeData = new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        JComboBox<Integer> shapeSizeBox = new JComboBox<>(shapeSizeData);
        shapeSizeBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                paintBoard.setPenSize(shapeSizeData[shapeSizeBox.getSelectedIndex()]);
            }
        });
        shapeOptionPanel.add(new JLabel("Pen size: "));
        shapeOptionPanel.add(shapeSizeBox);
        JButton buttonColorPicker = new JButton("Select color");
        buttonColorPicker.addActionListener(e -> {
            Color color = JColorChooser.showDialog(null, "Choose a color", paintBoard.getCurrentColor());
            paintBoard.setCurrentColor(color);
        });
        shapeOptionPanel.add(buttonColorPicker);

        Integer[] textSizeData = new Integer[]{14, 16, 18, 20, 22, 24, 26, 28, 30, 32};
        JComboBox<Integer> textSizeBox = new JComboBox<>(textSizeData);
        textSizeBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                paintBoard.setTextSize(textSizeData[textSizeBox.getSelectedIndex()]);
            }
        });
        textOpentionPanel.add(new JLabel("Text size: "));
        textOpentionPanel.add(textSizeBox);
        JButton buttonTextColorPicker = new JButton("Select color");
        buttonTextColorPicker.addActionListener(e -> {
            Color color = JColorChooser.showDialog(null, "Choose a color", paintBoard.getCurrentColor());
            paintBoard.setCurrentColor(color);
        });
        textOpentionPanel.add(buttonTextColorPicker);
        textOpentionPanel.add(new JLabel("Double click the canvas to add text..."));

        optionPanel.add("shapeOption", shapeOptionPanel);
        optionPanel.add("eraserOption", eraserOptionPanel);
        optionPanel.add("textOption", textOpentionPanel);

        optionMenu.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                switch (optionMenu.getSelectedIndex()) {
                    case 0:
                        paintBoard.setCurrentShape(ShapeType.LINE);
                        break;
                    case 1:
                        paintBoard.setCurrentShape(ShapeType.RECTANGLE);
                        break;
                    case 2:
                        paintBoard.setCurrentShape(ShapeType.CIRCLE);
                        break;
                    case 3:
                        paintBoard.setCurrentShape(ShapeType.OVAL);
                        break;
                    case 4:
                        paintBoard.setCurrentShape(ShapeType.FREE);
                        break;
                    case 5:
                        paintBoard.setCurrentShape(ShapeType.TEXT);
                        break;
                    case 6:
                        paintBoard.setCurrentShape(ShapeType.ERASER);
                        break;
                }
                if (optionMenu.getSelectedIndex() == 6) {
                    cardLayout.show(optionPanel, "eraserOption");
                } else if (optionMenu.getSelectedIndex() == 5) {
                    cardLayout.show(optionPanel, "textOption");
                } else {
                    cardLayout.show(optionPanel, "shapeOption");
                }
            }
        });
    }

    public synchronized void addShape(IShape shape) {
        paintBoard.addShapeWithRepaint(shape);
    }

    public synchronized void initShapes(List<IShape> shapes) {
        paintBoard.addShapesWithRepaint(shapes);
    }

    public synchronized void addMessage(ChatMessage message) {
        StringBuilder sb = new StringBuilder();
        sb.append(message.getUsername())
                .append("\n")
                .append(message.getTime())
                .append("\n")
                .append(message.getBody());
        chatBoard.appendMessage(sb.toString());
        chatBoard.appendMessage("\n");
    }

    public synchronized void initMessages(List<ChatMessage> messageList) {
        for (ChatMessage message : messageList) {
            addMessage(message);
        }
    }

    public synchronized void setUserList(List<String> userList) {
        userListBoard.setUserList(userList);
    }

    public abstract void initMenuBar();

    protected abstract void onWindowClosing();

    public void undo() {
        paintBoard.undo();
    }

    public void remoteUndo() {
        paintBoard.remoteUndo();
    }

    public void clear() {
        paintBoard.clearShapes();
    }

    public void remoteClear() {
        paintBoard.remoteClear();
    }

    public void reloadFromFile(List<IShape> shapes) {
        paintBoard.reloadFromFile(shapes);
    }

    public abstract void onServerDisconnected();
}
