
package chatty.gui.components.settings;

import chatty.gui.GuiUtil;
import chatty.lang.Language;
import chatty.util.DateTime;
import chatty.util.settings.FileManager;
import chatty.util.settings.FileManager.FileInfo;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Window;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author tduva
 */
public class BackupManager extends JDialog {
    
    private final JTable table;
    private final MyTableModel data;
    private final FileManager fileManager;
    private final JButton loadButton;
    private final JTextArea infoText;
    
    public BackupManager(Window parent, FileManager fileManager) {
        super(parent);
        setTitle(Language.getString("settings.backup.title"));
        setLayout(new GridBagLayout());
        
        this.fileManager = fileManager;
        data = new MyTableModel();
        table = new JTable(data) {
            
            @Override
            public String getToolTipText(MouseEvent e) {
                int row = rowAtPoint(e.getPoint());
                int column = columnAtPoint(e.getPoint());
                if (row == -1 || column == -1) {
                    return null;
                }
                FileInfo info = data.getRowData(convertRowIndexToModel(row));
                column = convertColumnIndexToModel(column);
                switch (column) {
                    case 0: return info.getFile().toString();
                    case 1: return String.format("%d bytes",
                            info.getSize());
                    case 2: return String.format("Modified: %s",
                            DateTime.formatFullDatetime(info.getModifiedTime()));
                    case 3: return info.getInfo();
                }
                return null;
            }
            
        };
        table.getColumnModel().getColumn(0).setPreferredWidth(200);
        table.getColumnModel().getColumn(1).setPreferredWidth(40);
        table.getColumnModel().getColumn(2).setPreferredWidth(80);
        table.getColumnModel().getColumn(3).setPreferredWidth(420);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> {
            update();
        });
        table.setAutoCreateRowSorter(true);
        
        GridBagConstraints gbc = GuiUtil.makeGbc(0, 0, 1, 1);
        
        add(new JLabel("<html><body width='600px'>"+SettingsUtil.getInfo("info-backup.html", null)), gbc);
        
        gbc = GuiUtil.makeGbc(0, 1, 1, 1);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 0.8;
        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setPreferredSize(new Dimension(table.getPreferredSize().width, 200));
        add(tableScroll, gbc);
        
        loadButton = new JButton("Load Selected Backup");
        loadButton.addActionListener(e -> {
            loadBackup();
        });
        gbc = GuiUtil.makeGbc(0, 2, 1, 1);
        add(loadButton, gbc);
        
        infoText = new JTextArea();
        infoText.setEditable(false);
        infoText.setRows(5);
        infoText.setMinimumSize(infoText.getPreferredSize());
        gbc = GuiUtil.makeGbc(0, 3, 1, 1);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 0.4;
        add(new JScrollPane(infoText), gbc);
    }
    
    private void update() {
        FileInfo selected = getSelected();
        loadButton.setEnabled(selected != null);
        if (selected != null) {
            infoText.setText(String.format("The following file will be copied when loading the backup:\n[From]\n %s\n[To]\n %s",
                    selected.getFile(), selected.getSettings().path));
        }
    }
    
    private FileInfo getSelected() {
        int selected = table.getSelectedRow();
        if (selected != -1) {
            return data.getRowData(table.convertRowIndexToModel(selected));
        }
        return null;
    }
    
    private void loadBackup() {
        FileInfo selected = getSelected();
        if (selected != null) {
            try {
                fileManager.loadBackup(selected);
                infoText.setText(String.format("[Copied]\n %s\n[To]\n %s\nYou may restart Chatty now or load another backup.",
                        selected.getFile(), selected.getSettings().path));
            }
            catch (IOException ex) {
                infoText.setText("Error loading backup: "+ex);
            }
        }
    }
    
    public void open() {
        try {
            data.setData(fileManager.getFileInfo());
        }
        catch (IOException ex) {
            infoText.setText("Error loading list: "+ex);
        }
        update();
        pack();
        setVisible(true);
    }
    
    private static class MyTableModel extends AbstractTableModel {
        
        private final String[] COLUMNS = {"Filename", "Size", "Modified", "Info"};
        
        private final List<FileInfo> data = new ArrayList<>();
        
        public void setData(List<FileManager.FileInfo> data) {
            this.data.clear();
            this.data.addAll(data);
            super.fireTableDataChanged();
        }

        @Override
        public int getRowCount() {
            return data.size();
        }

        @Override
        public int getColumnCount() {
            return COLUMNS.length;
        }
        
        public FileInfo getRowData(int index) {
            return data.get(index);
        }
        
        @Override
        public String getColumnName(int columnIndex) {
            return COLUMNS[columnIndex];
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            FileInfo info = data.get(rowIndex);
            switch (columnIndex) {
                case 0: return info.getFile().getFileName();
                case 1: return info.getSize();
                case 2: return DateTime.agoText(info.getModifiedTime());
                case 3: return info.getInfo();
                default: return null;
            }
        }

    }
    
}