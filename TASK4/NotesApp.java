import java.io.*;
import java.util.Scanner;

/**
 * Multi-Notes Manager Application
 * Demonstrates File I/O operations in Java with support for multiple note files
 * 
 * Features:
 * - Create multiple named notes
 * - List all notes
 * - Open and edit existing notes
 * - Delete specific notes
 * - Search across all notes
 * - Organize notes by category/tags
 */
public class NotesApp {
    
    private static final String NOTES_DIRECTORY = "my_notes";
    private static final String NOTES_INDEX_FILE = "notes_index.txt";
    private static final Scanner scanner = new Scanner(System.in);
    private static String currentNote = null;
    
    public static void main(String[] args) {
        // Create notes directory if it doesn't exist
        createNotesDirectory();
        
        System.out.println("=" .repeat(60));
        System.out.println("      WELCOME TO MULTI-NOTES MANAGER APP");
        System.out.println("=" .repeat(60));
        
        boolean running = true;
        
        while (running) {
            displayMainMenu();
            int choice = getValidIntegerInput("Enter your choice: ");
            
            switch (choice) {
                case 1:
                    createNewNote();
                    break;
                case 2:
                    listAllNotes();
                    break;
                case 3:
                    openNote();
                    break;
                case 4:
                    editCurrentNote();
                    break;
                case 5:
                    appendToCurrentNote();
                    break;
                case 6:
                    deleteNote();
                    break;
                case 7:
                    searchAllNotes();
                    break;
                case 8:
                    organizeNotesByTag();
                    break;
                case 9:
                    exportAllNotes();
                    break;
                case 10:
                    running = false;
                    System.out.println("\nThank you for using Multi-Notes Manager!");
                    System.out.println("Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice! Please try again.");
            }
        }
        
        scanner.close();
    }
    
    /**
     * Creates the notes directory if it doesn't exist
     */
    private static void createNotesDirectory() {
        File dir = new File(NOTES_DIRECTORY);
        if (!dir.exists()) {
            if (dir.mkdir()) {
                System.out.println("✓ Notes directory created: " + NOTES_DIRECTORY);
            } else {
                System.out.println("✗ Failed to create notes directory!");
            }
        }
    }
    
    /**
     * Displays the main menu options
     */
    private static void displayMainMenu() {
        System.out.println("\n" + "-" .repeat(60));
        System.out.println("MAIN MENU:");
        System.out.println("-" .repeat(60));
        System.out.println("1. Create New Note");
        System.out.println("2. List All Notes");
        System.out.println("3. Open a Note");
        System.out.println("4. Edit Current Note (" + (currentNote != null ? currentNote : "None selected") + ")");
        System.out.println("5. Append to Current Note");
        System.out.println("6. Delete a Note");
        System.out.println("7. Search Across All Notes");
        System.out.println("8. Organize Notes by Tag");
        System.out.println("9. Export All Notes");
        System.out.println("10. Exit");
        System.out.println("-" .repeat(60));
        if (currentNote != null) {
            System.out.println("📝 Current Note: " + currentNote);
        }
    }
    
    /**
     * Creates a new note file
     */
    private static void createNewNote() {
        System.out.println("\n--- CREATE NEW NOTE ---");
        System.out.print("Enter note name (without spaces, e.g., 'meeting_notes'): ");
        String noteName = scanner.nextLine().trim();
        
        if (noteName.isEmpty()) {
            System.out.println("Note name cannot be empty!");
            return;
        }
        
        // Clean the filename
        noteName = noteName.replaceAll("[^a-zA-Z0-9_-]", "_");
        String filename = noteName + ".txt";
        String filePath = NOTES_DIRECTORY + File.separator + filename;
        
        File file = new File(filePath);
        if (file.exists()) {
            System.out.println("✗ A note with this name already exists!");
            System.out.print("Do you want to open it instead? (yes/no): ");
            if (scanner.nextLine().equalsIgnoreCase("yes")) {
                currentNote = filePath;
                viewNoteContent();
            }
            return;
        }
        
        System.out.print("Add tags (comma-separated, e.g., work,personal,ideas): ");
        String tags = scanner.nextLine();
        
        System.out.println("Enter your note content (type 'SAVE' on a new line to finish):");
        
        StringBuilder noteContent = new StringBuilder();
        
        // Add metadata header
        noteContent.append("---METADATA---\n");
        noteContent.append("Created: ").append(new java.util.Date()).append("\n");
        noteContent.append("Tags: ").append(tags).append("\n");
        noteContent.append("---CONTENT---\n");
        
        try (FileWriter writer = new FileWriter(filePath)) {
            
            while (true) {
                String line = scanner.nextLine();
                if (line.equalsIgnoreCase("SAVE")) {
                    break;
                }
                noteContent.append(line).append("\n");
            }
            
            writer.write(noteContent.toString());
            
            // Update notes index
            addToNotesIndex(noteName, tags, filePath);
            
            currentNote = filePath;
            System.out.println("✓ Note '" + noteName + "' created successfully!");
            System.out.println("  Location: " + filePath);
            
        } catch (IOException e) {
            handleException("Error creating note", e);
        }
    }
    
    /**
     * Adds a note to the master index file for easy management
     */
    private static void addToNotesIndex(String noteName, String tags, String filePath) {
        try (FileWriter writer = new FileWriter(NOTES_DIRECTORY + File.separator + NOTES_INDEX_FILE, true)) {
            writer.write(noteName + "|" + tags + "|" + new java.util.Date() + "|" + filePath + "\n");
        } catch (IOException e) {
            System.err.println("Warning: Could not update notes index: " + e.getMessage());
        }
    }
    
    /**
     * Lists all available notes with their metadata
     */
    private static void listAllNotes() {
        System.out.println("\n--- ALL NOTES ---");
        File notesDir = new File(NOTES_DIRECTORY);
        File[] files = notesDir.listFiles((dir, name) -> name.endsWith(".txt") && !name.equals(NOTES_INDEX_FILE));
        
        if (files == null || files.length == 0) {
            System.out.println("No notes found. Create your first note using Option 1!");
            return;
        }
        
        System.out.println("\n" + "=" .repeat(60));
        System.out.println("YOUR NOTES:");
        System.out.println("=" .repeat(60));
        
        int index = 1;
        for (File file : files) {
            String noteName = file.getName().replace(".txt", "");
            System.out.println(index + ". " + noteName);
            System.out.println("   📁 Location: " + file.getPath());
            System.out.println("   📏 Size: " + file.length() + " bytes");
            System.out.println("   📅 Modified: " + new java.util.Date(file.lastModified()));
            
            // Read and display tags from the file
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line = reader.readLine();
                if (line != null && line.equals("---METADATA---")) {
                    while ((line = reader.readLine()) != null && !line.equals("---CONTENT---")) {
                        if (line.startsWith("Tags: ")) {
                            System.out.println("   🏷️  " + line);
                        }
                    }
                }
            } catch (IOException e) {
                // Skip tag reading if error
            }
            System.out.println();
            index++;
        }
    }
    
    /**
     * Opens an existing note for viewing and editing
     */
    private static void openNote() {
        System.out.println("\n--- OPEN A NOTE ---");
        System.out.print("Enter note name to open: ");
        String noteName = scanner.nextLine().trim();
        
        if (noteName.isEmpty()) {
            System.out.println("Note name cannot be empty!");
            return;
        }
        
        noteName = noteName.replaceAll("[^a-zA-Z0-9_-]", "_");
        String filename = noteName + ".txt";
        String filePath = NOTES_DIRECTORY + File.separator + filename;
        
        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("✗ Note '" + noteName + "' not found!");
            System.out.print("Do you want to create it? (yes/no): ");
            if (scanner.nextLine().equalsIgnoreCase("yes")) {
                createNewNote();
            }
            return;
        }
        
        currentNote = filePath;
        viewNoteContent();
    }
    
    /**
     * Views the content of the current note
     */
    private static void viewNoteContent() {
        if (currentNote == null) {
            System.out.println("No note selected! Please open a note first (Option 3).");
            return;
        }
        
        System.out.println("\n--- VIEWING NOTE: " + new File(currentNote).getName().replace(".txt", "") + " ---");
        
        try (BufferedReader reader = new BufferedReader(new FileReader(currentNote))) {
            String line;
            boolean inContent = false;
            int lineNumber = 0;
            
            System.out.println("\n" + "=" .repeat(50));
            
            while ((line = reader.readLine()) != null) {
                if (line.equals("---CONTENT---")) {
                    inContent = true;
                    continue;
                }
                if (!inContent && line.startsWith("---")) {
                    continue;
                }
                if (inContent) {
                    lineNumber++;
                    System.out.println(lineNumber + ". " + line);
                } else if (!line.startsWith("---")) {
                    System.out.println(line);
                }
            }
            
            if (lineNumber == 0) {
                System.out.println("(Empty note)");
            }
            
            System.out.println("=" .repeat(50));
            System.out.println("Total lines: " + lineNumber);
            
        } catch (FileNotFoundException e) {
            System.out.println("✗ Note file not found!");
            currentNote = null;
        } catch (IOException e) {
            handleException("Error reading note", e);
        }
    }
    
    /**
     * Edits the current note (overwrite mode)
     */
    private static void editCurrentNote() {
        if (currentNote == null) {
            System.out.println("No note selected! Please open a note first (Option 3).");
            return;
        }
        
        System.out.println("\n--- EDITING NOTE: " + new File(currentNote).getName().replace(".txt", "") + " ---");
        System.out.println("Current content will be REPLACED.");
        System.out.println("Enter new content (type 'SAVE' on a new line to finish):");
        
        StringBuilder newContent = new StringBuilder();
        
        // Read existing metadata
        String metadata = "";
        try (BufferedReader reader = new BufferedReader(new FileReader(currentNote))) {
            StringBuilder metaBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                metaBuilder.append(line).append("\n");
                if (line.equals("---CONTENT---")) {
                    break;
                }
            }
            metadata = metaBuilder.toString();
        } catch (IOException e) {
            System.err.println("Error reading metadata: " + e.getMessage());
        }
        
        // Add the content section header
        if (!metadata.contains("---CONTENT---")) {
            newContent.append("---METADATA---\n");
            newContent.append("Modified: ").append(new java.util.Date()).append("\n");
            newContent.append("---CONTENT---\n");
        } else {
            newContent.append(metadata);
        }
        
        // Get new content
        while (true) {
            String line = scanner.nextLine();
            if (line.equalsIgnoreCase("SAVE")) {
                break;
            }
            newContent.append(line).append("\n");
        }
        
        // Write back to file
        try (FileWriter writer = new FileWriter(currentNote, false)) {
            writer.write(newContent.toString());
            System.out.println("✓ Note updated successfully!");
        } catch (IOException e) {
            handleException("Error updating note", e);
        }
    }
    
    /**
     * Appends to the current note
     */
    private static void appendToCurrentNote() {
        if (currentNote == null) {
            System.out.println("No note selected! Please open a note first (Option 3).");
            return;
        }
        
        System.out.println("\n--- APPENDING TO NOTE: " + new File(currentNote).getName().replace(".txt", "") + " ---");
        System.out.println("Enter content to append (type 'SAVE' on a new line to finish):");
        
        StringBuilder appendContent = new StringBuilder();
        
        while (true) {
            String line = scanner.nextLine();
            if (line.equalsIgnoreCase("SAVE")) {
                break;
            }
            appendContent.append(line).append("\n");
        }
        
        try (FileWriter writer = new FileWriter(currentNote, true)) {
            writer.write(appendContent.toString());
            System.out.println("✓ Content appended successfully!");
        } catch (IOException e) {
            handleException("Error appending to note", e);
        }
    }
    
    /**
     * Deletes a specific note
     */
    private static void deleteNote() {
        System.out.println("\n--- DELETE NOTE ---");
        listAllNotes();
        
        System.out.print("Enter the exact note name to delete: ");
        String noteName = scanner.nextLine().trim();
        
        if (noteName.isEmpty()) {
            System.out.println("Note name cannot be empty!");
            return;
        }
        
        noteName = noteName.replaceAll("[^a-zA-Z0-9_-]", "_");
        String filename = noteName + ".txt";
        String filePath = NOTES_DIRECTORY + File.separator + filename;
        
        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("✗ Note '" + noteName + "' not found!");
            return;
        }
        
        System.out.print("Are you sure you want to delete '" + noteName + "'? (yes/no): ");
        if (scanner.nextLine().equalsIgnoreCase("yes")) {
            if (file.delete()) {
                System.out.println("✓ Note '" + noteName + "' deleted successfully!");
                if (currentNote != null && currentNote.equals(filePath)) {
                    currentNote = null;
                }
            } else {
                System.out.println("✗ Failed to delete the note.");
            }
        } else {
            System.out.println("Deletion cancelled.");
        }
    }
    
    /**
     * Searches for text across all notes
     */
    private static void searchAllNotes() {
        System.out.println("\n--- SEARCH ACROSS ALL NOTES ---");
        System.out.print("Enter search term: ");
        String searchTerm = scanner.nextLine();
        
        if (searchTerm.trim().isEmpty()) {
            System.out.println("Search term cannot be empty!");
            return;
        }
        
        File notesDir = new File(NOTES_DIRECTORY);
        File[] files = notesDir.listFiles((dir, name) -> name.endsWith(".txt") && !name.equals(NOTES_INDEX_FILE));
        
        if (files == null || files.length == 0) {
            System.out.println("No notes to search through!");
            return;
        }
        
        System.out.println("\nSearch results for: \"" + searchTerm + "\"");
        System.out.println("=" .repeat(60));
        
        int matchCount = 0;
        
        for (File file : files) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                int lineNumber = 0;
                boolean inContent = false;
                boolean foundInFile = false;
                
                while ((line = reader.readLine()) != null) {
                    if (line.equals("---CONTENT---")) {
                        inContent = true;
                        continue;
                    }
                    if (!inContent) {
                        continue;
                    }
                    lineNumber++;
                    if (line.toLowerCase().contains(searchTerm.toLowerCase())) {
                        if (!foundInFile) {
                            System.out.println("\n📄 In: " + file.getName().replace(".txt", ""));
                            foundInFile = true;
                        }
                        System.out.println("   Line " + lineNumber + ": " + line);
                        matchCount++;
                    }
                }
            } catch (IOException e) {
                // Skip files that can't be read
            }
        }
        
        if (matchCount == 0) {
            System.out.println("\nNo matches found for \"" + searchTerm + "\" in any note.");
        } else {
            System.out.println("\n✓ Found " + matchCount + " matches in total.");
        }
    }
    
    /**
     * Organizes notes by tags (creates tag-based folders)
     */
    private static void organizeNotesByTag() {
        System.out.println("\n--- ORGANIZE NOTES BY TAG ---");
        System.out.println("This will create tag-based folders and organize your notes.");
        System.out.print("Do you want to proceed? (yes/no): ");
        
        if (!scanner.nextLine().equalsIgnoreCase("yes")) {
            return;
        }
        
        File notesDir = new File(NOTES_DIRECTORY);
        File[] files = notesDir.listFiles((dir, name) -> name.endsWith(".txt") && !name.equals(NOTES_INDEX_FILE));
        
        if (files == null || files.length == 0) {
            System.out.println("No notes to organize!");
            return;
        }
        
        for (File file : files) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                String tags = "";
                
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("Tags: ")) {
                        tags = line.substring(6);
                        break;
                    }
                    if (line.equals("---CONTENT---")) {
                        break;
                    }
                }
                
                if (!tags.isEmpty()) {
                    String[] tagList = tags.split(",");
                    for (String tag : tagList) {
                        tag = tag.trim();
                        File tagDir = new File(NOTES_DIRECTORY + File.separator + "by_tag" + File.separator + tag);
                        if (!tagDir.exists()) {
                            tagDir.mkdirs();
                        }
                        // Create a symbolic link or copy reference
                        try (FileWriter refWriter = new FileWriter(tagDir + File.separator + file.getName() + ".ref")) {
                            refWriter.write("Reference to: " + file.getAbsolutePath() + "\n");
                            refWriter.write("Note: " + file.getName().replace(".txt", "") + "\n");
                            refWriter.write("Tags: " + tags);
                        }
                    }
                }
            } catch (IOException e) {
                // Skip files with errors
            }
        }
        
        System.out.println("✓ Notes organized by tag in the 'by_tag' folder!");
    }
    
    /**
     * Exports all notes to a single file
     */
    private static void exportAllNotes() {
        System.out.println("\n--- EXPORT ALL NOTES ---");
        String exportFile = NOTES_DIRECTORY + File.separator + "all_notes_export_" + 
                           System.currentTimeMillis() + ".txt";
        
        try (FileWriter writer = new FileWriter(exportFile)) {
            File notesDir = new File(NOTES_DIRECTORY);
            File[] files = notesDir.listFiles((dir, name) -> name.endsWith(".txt") && !name.equals(NOTES_INDEX_FILE));
            
            if (files == null || files.length == 0) {
                System.out.println("No notes to export!");
                return;
            }
            
            writer.write("=" .repeat(80) + "\n");
            writer.write("NOTES EXPORT - " + new java.util.Date() + "\n");
            writer.write("=" .repeat(80) + "\n\n");
            
            for (File file : files) {
                writer.write("\n" + "-" .repeat(60) + "\n");
                writer.write("NOTE: " + file.getName().replace(".txt", "") + "\n");
                writer.write("-" .repeat(60) + "\n");
                
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        writer.write(line + "\n");
                    }
                }
                writer.write("\n");
            }
            
            System.out.println("✓ All notes exported successfully to: " + exportFile);
            
        } catch (IOException e) {
            handleException("Error exporting notes", e);
        }
    }
    
    /**
     * Gets valid integer input from user
     */
    private static int getValidIntegerInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a number.");
            }
        }
    }
    
    /**
     * Handles exceptions with proper logging
     */
    private static void handleException(String message, IOException e) {
        System.err.println("\n✗ ERROR: " + message);
        System.err.println("  Details: " + e.getMessage());
        System.err.println("  Stack Trace: ");
        e.printStackTrace();
        logException(e);
    }
    
    /**
     * Logs exceptions to a separate log file
     */
    private static void logException(Exception e) {
        File logDir = new File(NOTES_DIRECTORY);
        if (!logDir.exists()) {
            logDir.mkdir();
        }
        
        try (FileWriter logWriter = new FileWriter(NOTES_DIRECTORY + File.separator + "error_log.txt", true)) {
            logWriter.write("[" + new java.util.Date() + "] ");
            logWriter.write("Exception: " + e.toString() + "\n");
            for (StackTraceElement element : e.getStackTrace()) {
                logWriter.write("  at " + element.toString() + "\n");
            }
            logWriter.write("-" .repeat(50) + "\n");
        } catch (IOException logError) {
            System.err.println("Could not write to error log: " + logError.getMessage());
        }
    }
}