package gamehex.view;

import gamehex.model.ScoreBoard;
import gamehex.model.ScoreEntry;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

public final class HistoryPanel extends JPanel {
    private final DefaultTableModel model;
    private final JTable table;

    private static final String[] COLS = {"#", "Player", "Duration", "Date"};

    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneId.systemDefault());


    private static final Font FANCY_FONT = new Font("Papyrus", Font.BOLD, 16);

    public HistoryPanel(ScoreBoard board, Runnable onBack){
        super(new BorderLayout());

        JPanel root = Theme.gradientPanel(new BorderLayout());
        add(root, BorderLayout.CENTER);

        JLabel title = new JLabel("Players History ");
        title.setForeground(Theme.TEXT);
        title.setFont(new Font("Papyrus", Font.BOLD, 22));
        title.setBorder(new EmptyBorder(10,12,10,12));

        model = new DefaultTableModel(COLS, 0){
            @Override public boolean isCellEditable(int r,int c){ return false; }
        };
        table = new JTable(model);
        table.setFont(FANCY_FONT);
        table.setRowHeight(30);
        table.setForeground(Theme.TEXT);
        table.setBackground(new Color(0,0,0,0));
        table.setOpaque(false);

        // header
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Papyrus", Font.BOLD, 18));
        header.setBackground(Color.BLACK);
        header.setForeground(Theme.SUBTEXT);

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);
        for (int i=0;i<table.getColumnCount();i++){
            table.getColumnModel().getColumn(i).setCellRenderer(center);
        }

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getViewport().setOpaque(false);
        sp.setOpaque(false);

        JButton back = Theme.neonButton("Back to Menu");
        back.setFont(new Font("Papyrus", Font.BOLD, 16));
        back.addActionListener(e -> onBack.run());

        root.add(title, BorderLayout.NORTH);
        root.add(sp, BorderLayout.CENTER);

        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.setOpaque(false);
        south.add(back);
        root.add(south, BorderLayout.SOUTH);

        refresh(board);
    }

    public void refresh(ScoreBoard board){
        model.setRowCount(0);
        List<ScoreEntry> items = board.allSortedDesc();
        if (items.isEmpty()){
            model.addRow(new Object[]{"—","No Records Yet","—","—"});
        } else {
            int idx=1;
            for (ScoreEntry sc: items){
                long ms = sc.millis();
                long s = ms/1000, rem = ms%1000;
                String duration = String.format("%d.%03ds", s, rem);
                String date = DATE_FMT.format(Instant.ofEpochMilli(sc.epochMs()));
                model.addRow(new Object[]{idx++, sc.player(), duration, date});
            }
        }
    }
}
