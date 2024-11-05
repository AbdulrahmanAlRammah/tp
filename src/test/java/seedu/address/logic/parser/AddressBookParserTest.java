package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.LIST_MESSAGE_INVALID_COMMAND;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_UNKNOWN_COMMAND;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_REMARK;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.ClearCommand;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.logic.commands.ExitCommand;
import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.commands.RemarkCommand;
import seedu.address.logic.commands.SeedCommand;
import seedu.address.logic.commands.SortCommand;
import seedu.address.logic.commands.ViewCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.AddressContainsKeywordsPredicate;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonComparator;
import seedu.address.model.person.PhoneContainsKeywordsPredicate;
import seedu.address.model.person.Remark;
import seedu.address.model.person.TagContainsKeywordsPredicate;
import seedu.address.testutil.EditPersonDescriptorBuilder;
import seedu.address.testutil.PersonBuilder;
import seedu.address.testutil.PersonUtil;

public class AddressBookParserTest {

    private final AddressBookParser parser = new AddressBookParser();

    @Test
    public void parseCommand_add() throws Exception {
        Person person = new PersonBuilder().build();
        AddCommand command = (AddCommand) parser.parseCommand(PersonUtil.getAddCommand(person));
        assertEquals(new AddCommand(person), command);
    }

    @Test
    public void parseCommand_clear() throws Exception {
        assertTrue(parser.parseCommand(ClearCommand.COMMAND_WORD) instanceof ClearCommand);
        assertTrue(parser.parseCommand(ClearCommand.COMMAND_WORD + " 3") instanceof ClearCommand);
    }

    @Test
    public void parseCommand_delete() throws Exception {
        DeleteCommand command = (DeleteCommand) parser.parseCommand(
                DeleteCommand.COMMAND_WORD + " " + INDEX_FIRST_PERSON.getOneBased());
        assertEquals(new DeleteCommand(INDEX_FIRST_PERSON), command);
    }

    @Test
    public void parseCommand_edit() throws Exception {
        Person person = new PersonBuilder().build();
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder(person).build();
        EditCommand command = (EditCommand) parser.parseCommand(EditCommand.COMMAND_WORD + " "
                + INDEX_FIRST_PERSON.getOneBased() + " " + PersonUtil.getEditPersonDescriptorDetails(descriptor));
        assertEquals(new EditCommand(INDEX_FIRST_PERSON, descriptor), command);
    }

    @Test
    public void parseCommand_exit() throws Exception {
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD) instanceof ExitCommand);
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD + " 3") instanceof ExitCommand);
    }

    @Test
    public void parseCommand_find() throws Exception {
        // Test find on name
        List<String> nameKeywords = Arrays.asList("foo", "bar");
        FindCommand nameCommand = (FindCommand) parser.parseCommand(
                FindCommand.COMMAND_WORD + " " + PREFIX_NAME
                        + nameKeywords.stream().collect(Collectors.joining(" ")));
        assertEquals(new FindCommand(new NameContainsKeywordsPredicate(nameKeywords)), nameCommand);

        // Test find on phone
        List<String> phoneKeywords = Arrays.asList("12345678", "87654321");
        FindCommand phoneCommand = (FindCommand) parser.parseCommand(
                FindCommand.COMMAND_WORD + " " + PREFIX_PHONE
                        + phoneKeywords.stream().collect(Collectors.joining(" ")));
        assertEquals(new FindCommand(new PhoneContainsKeywordsPredicate(phoneKeywords)), phoneCommand);

        // Test find on address
        List<String> addressKeywords = Arrays.asList("blk 50", "blk 51");
        FindCommand addressCommand = (FindCommand) parser.parseCommand(
                FindCommand.COMMAND_WORD + " " + PREFIX_ADDRESS
                        + addressKeywords.stream().collect(Collectors.joining("_")));
        assertEquals(new FindCommand(new AddressContainsKeywordsPredicate(addressKeywords)), addressCommand);

        // Test find on tag
        List<String> tagKeywords = Arrays.asList("friends", "colleagues");
        FindCommand tagCommand = (FindCommand) parser.parseCommand(
                FindCommand.COMMAND_WORD + " " + PREFIX_TAG
                        + tagKeywords.stream().collect(Collectors.joining("_")));
        assertEquals(new FindCommand(new TagContainsKeywordsPredicate(tagKeywords)), tagCommand);

        // Test find on name and phone
        FindCommand namePhoneCommand = (FindCommand) parser.parseCommand(
                FindCommand.COMMAND_WORD + " " + PREFIX_NAME + "foo bar "
                        + PREFIX_PHONE + "12345678");
        assertEquals(new FindCommand(
                        new NameContainsKeywordsPredicate(Arrays.asList("foo", "bar")),
                        new PhoneContainsKeywordsPredicate(Arrays.asList("12345678"))),
                namePhoneCommand);

        // Test find on name and address
        FindCommand nameAddressCommand = (FindCommand) parser.parseCommand(
                FindCommand.COMMAND_WORD + " " + PREFIX_NAME + "foo bar "
                        + PREFIX_ADDRESS + "blk 50");
        assertEquals(new FindCommand(
                        new NameContainsKeywordsPredicate(Arrays.asList("foo", "bar")),
                        new AddressContainsKeywordsPredicate(Arrays.asList("blk 50"))),
                nameAddressCommand);

        // Test find on name and tag
        FindCommand nameTagCommand = (FindCommand) parser.parseCommand(
                FindCommand.COMMAND_WORD + " " + PREFIX_NAME + "foo bar "
                        + PREFIX_TAG + "friends");
        assertEquals(new FindCommand(
                        new NameContainsKeywordsPredicate(Arrays.asList("foo", "bar")),
                        new TagContainsKeywordsPredicate(Arrays.asList("friends"))),
                nameTagCommand);

        // Test find on phone and address
        FindCommand phoneAddressCommand = (FindCommand) parser.parseCommand(
                FindCommand.COMMAND_WORD + " " + PREFIX_PHONE + "12345678 "
                        + PREFIX_ADDRESS + "blk 50");
        assertEquals(new FindCommand(
                        new PhoneContainsKeywordsPredicate(Arrays.asList("12345678")),
                        new AddressContainsKeywordsPredicate(Arrays.asList("blk 50"))),
                phoneAddressCommand);

        // Test find on phone and tag
        FindCommand phoneTagCommand = (FindCommand) parser.parseCommand(
                FindCommand.COMMAND_WORD + " " + PREFIX_PHONE + "12345678 "
                        + PREFIX_TAG + "friends");
        assertEquals(new FindCommand(
                        new PhoneContainsKeywordsPredicate(Arrays.asList("12345678")),
                        new TagContainsKeywordsPredicate(Arrays.asList("friends"))),
                phoneTagCommand);

        // Test find on address and tag
        FindCommand addressTagCommand = (FindCommand) parser.parseCommand(
                FindCommand.COMMAND_WORD + " " + PREFIX_ADDRESS + "blk 50 "
                        + PREFIX_TAG + "friends");
        assertEquals(new FindCommand(
                        new AddressContainsKeywordsPredicate(Arrays.asList("blk 50")),
                        new TagContainsKeywordsPredicate(Arrays.asList("friends"))),
                addressTagCommand);

        // Test find on name, phone and address
        FindCommand namePhoneAddressCommand = (FindCommand) parser.parseCommand(
                FindCommand.COMMAND_WORD + " " + PREFIX_NAME + "foo bar "
                        + PREFIX_PHONE + "12345678 "
                        + PREFIX_ADDRESS + "blk 50");
        assertEquals(new FindCommand(
                        new NameContainsKeywordsPredicate(Arrays.asList("foo", "bar")),
                        new PhoneContainsKeywordsPredicate(Arrays.asList("12345678")),
                        new AddressContainsKeywordsPredicate(Arrays.asList("blk 50"))),
                namePhoneAddressCommand);

        // Test find on name, phone and tag
        FindCommand namePhoneTagCommand = (FindCommand) parser.parseCommand(
                FindCommand.COMMAND_WORD + " " + PREFIX_NAME + "foo bar "
                        + PREFIX_PHONE + "12345678 "
                        + PREFIX_TAG + "friends");
        assertEquals(new FindCommand(
                        new NameContainsKeywordsPredicate(Arrays.asList("foo", "bar")),
                        new PhoneContainsKeywordsPredicate(Arrays.asList("12345678")),
                        new TagContainsKeywordsPredicate(Arrays.asList("friends"))),
                namePhoneTagCommand);

        // Test find on name, address and tag
        FindCommand nameAddressTagCommand = (FindCommand) parser.parseCommand(
                FindCommand.COMMAND_WORD + " " + PREFIX_NAME + "foo bar "
                        + PREFIX_ADDRESS + "blk 50 "
                        + PREFIX_TAG + "friends");
        assertEquals(new FindCommand(
                        new NameContainsKeywordsPredicate(Arrays.asList("foo", "bar")),
                        new AddressContainsKeywordsPredicate(Arrays.asList("blk 50")),
                        new TagContainsKeywordsPredicate(Arrays.asList("friends"))),
                nameAddressTagCommand);

        // Test find on phone, address and tag
        FindCommand phoneAddressTagCommand = (FindCommand) parser.parseCommand(
                FindCommand.COMMAND_WORD + " " + PREFIX_PHONE + "12345678 "
                        + PREFIX_ADDRESS + "blk 50 "
                        + PREFIX_TAG + "friends");
        assertEquals(new FindCommand(
                        new PhoneContainsKeywordsPredicate(Arrays.asList("12345678")),
                        new AddressContainsKeywordsPredicate(Arrays.asList("blk 50")),
                        new TagContainsKeywordsPredicate(Arrays.asList("friends"))),
                phoneAddressTagCommand);

        // Test find on all fields
        FindCommand allFieldsCommand = (FindCommand) parser.parseCommand(
                FindCommand.COMMAND_WORD + " " + PREFIX_NAME + "foo bar "
                        + PREFIX_PHONE + "12345678 "
                        + PREFIX_ADDRESS + "blk 50 "
                        + PREFIX_TAG + "friends");
        assertEquals(new FindCommand(
                        new NameContainsKeywordsPredicate(Arrays.asList("foo", "bar")),
                        new PhoneContainsKeywordsPredicate(Arrays.asList("12345678")),
                        new AddressContainsKeywordsPredicate(Arrays.asList("blk 50")),
                        new TagContainsKeywordsPredicate(Arrays.asList("friends"))),
                allFieldsCommand);
    }

    @Test
    public void parseCommand_help() throws Exception {
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD) instanceof HelpCommand);
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD + " 3") instanceof HelpCommand);
    }

    @Test
    public void parseCommand_capitalHelp_success() throws Exception {
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD.toUpperCase()) instanceof HelpCommand);
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD.toUpperCase() + " 3") instanceof HelpCommand);
    }

    @Test
    public void parseCommand_list() throws Exception {
        assertTrue(parser.parseCommand(ListCommand.COMMAND_WORD) instanceof ListCommand);

        // Test changed to match list command format of SocialBook
        assertThrows(ParseException.class, String.format(LIST_MESSAGE_INVALID_COMMAND), ()
                -> parser.parseCommand(ListCommand.COMMAND_WORD + " 3"));
    }

    @Test
    public void parseCommand_unrecognisedInput_throwsParseException() {
        assertThrows(ParseException.class, String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE), ()
            -> parser.parseCommand(""));
    }

    @Test
    public void parseCommand_unknownCommand_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_UNKNOWN_COMMAND, () -> parser.parseCommand("unknownCommand"));
    }

    @Test
    public void parseCommand_seed() throws Exception {
        assertTrue(parser.parseCommand(SeedCommand.COMMAND_WORD) instanceof SeedCommand);
    }

    @Test
    public void parseCommand_remark() throws Exception {
        final Remark remark = new Remark("Test remark command");
        RemarkCommand command = (RemarkCommand) parser.parseCommand(RemarkCommand.COMMAND_WORD + " "
                + INDEX_FIRST_PERSON.getOneBased() + " " + PREFIX_REMARK + remark.value);
        assertEquals(new RemarkCommand(INDEX_FIRST_PERSON, remark), command);
    }

    @Test
    public void parseCommand_sort() throws Exception {
        SortCommand command = (SortCommand) parser.parseCommand(SortCommand.COMMAND_WORD + " "
                + PREFIX_NAME);
        assertEquals(new SortCommand(PersonComparator.NAME, true), command);
    }

    @Test
    public void parseCommand_view() throws Exception {
        ViewCommand command = (ViewCommand) parser.parseCommand(ViewCommand.COMMAND_WORD + " "
                + INDEX_FIRST_PERSON.getOneBased());
        assertEquals(new ViewCommand(INDEX_FIRST_PERSON), command);
    }
}
