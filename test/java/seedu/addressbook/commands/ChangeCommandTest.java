package seedu.addressbook.commands;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import seedu.addressbook.common.Messages;
import seedu.addressbook.data.AddressBook;
import seedu.addressbook.data.exception.IllegalValueException;
import seedu.addressbook.data.person.*;
import seedu.addressbook.data.person.UniquePersonList.PersonNotFoundException;
import seedu.addressbook.ui.TextUi;
import seedu.addressbook.util.TestUtil;

public class ChangeCommandTest {

    private AddressBook emptyAddressBook;
    private AddressBook addressBook;

    private List<ReadOnlyPerson> emptyDisplayList;
    private List<ReadOnlyPerson> listWithEveryone;
    private List<ReadOnlyPerson> listWithSurnameDoe;
    public static final String MESSAGE_DUPLICATE_PERSON = "This person already exists in the address book";

    @Before
    public void setUp() throws Exception {
        Person johnDoe = new Person(new Name("John Doe"), new Phone("61234567", false),
                new Email("john@doe.com", false), new Address("395C Ben Road", false), Collections.emptySet());
        Person janeDoe = new Person(new Name("Jane Doe"), new Phone("91234567", false),
                new Email("jane@doe.com", false), new Address("33G Ohm Road", false), Collections.emptySet());
        Person samDoe = new Person(new Name("Sam Doe"), new Phone("63345566", false),
                new Email("sam@doe.com", false), new Address("55G Abc Road", false), Collections.emptySet());
        Person davidGrant = new Person(new Name("David Grant"), new Phone("61121122", false),
                new Email("david@grant.com", false), new Address("44H Define Road", false),
                Collections.emptySet());

        emptyAddressBook = TestUtil.createAddressBook();
        addressBook = TestUtil.createAddressBook(johnDoe, janeDoe, davidGrant, samDoe);

        emptyDisplayList = TestUtil.createList();

        listWithEveryone = TestUtil.createList(johnDoe, janeDoe, davidGrant, samDoe);
        listWithSurnameDoe = TestUtil.createList(johnDoe, janeDoe, samDoe);
    }

    @Test
    public void execute_emptyAddressBook_returnsPersonNotFoundMessage() {
        assertChangeFailsDueToNoSuchPerson(1, emptyAddressBook, listWithEveryone);
    }

    @Test
    public void execute_noPersonDisplayed_returnsInvalidIndexMessage() {
        assertChangeFailsDueToInvalidIndex(1, addressBook, emptyDisplayList);
    }
    

    @Test
    public void execute_invalidIndex_returnsInvalidIndexMessage() {
        assertChangeFailsDueToInvalidIndex(0, addressBook, listWithEveryone);
        assertChangeFailsDueToInvalidIndex(-1, addressBook, listWithEveryone);
        assertChangeFailsDueToInvalidIndex(listWithEveryone.size() + 1, addressBook, listWithEveryone);
    }

    @Test
    public void execute_validIndex_personChange() throws PersonNotFoundException {
        assertChangeSuccessful(1, addressBook, listWithSurnameDoe);
        assertChangeSuccessful(listWithSurnameDoe.size(), addressBook, listWithSurnameDoe);

        int middleIndex = (listWithSurnameDoe.size() / 2) + 1;
        assertChangeSuccessful(middleIndex, addressBook, listWithSurnameDoe);
    }

    /**
     * Creates a new Change command.
     *
     * @param targetVisibleIndex of the person that we want to Change
     */
    private ChangeCommand createChangeCommand(int targetVisibleIndex, AddressBook addressBook,
                                              List<ReadOnlyPerson> displayList) {

        ChangeCommand command = new ChangeCommand(targetVisibleIndex, "777777");
        command.setData(addressBook, displayList);

        return command;
    }

    /**
     * Executes the command, and checks that the execution was what we had expected.
     */
    private void assertCommandBehaviour(ChangeCommand ChangeCommand, String expectedMessage,
                                        AddressBook expectedAddressBook, AddressBook actualAddressBook) {

        CommandResult result = ChangeCommand.execute();

        assertEquals(expectedMessage, result.feedbackToUser);
        assertEquals(expectedAddressBook.getAllPersons(), actualAddressBook.getAllPersons());
    }

    /**
     * Asserts that the index is not valid for the given display list.
     */
    private void assertChangeFailsDueToInvalidIndex(int invalidVisibleIndex, AddressBook addressBook,
                                                      List<ReadOnlyPerson> displayList) {

        String expectedMessage = Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX;

        ChangeCommand command = createChangeCommand(invalidVisibleIndex, addressBook, displayList);
        assertCommandBehaviour(command, expectedMessage, addressBook, addressBook);
    }

    /**
     * Asserts that the person at the specified index cannot be Changed, because that person
     * is not in the address book.
     */
    private void assertChangeFailsDueToNoSuchPerson(int visibleIndex, AddressBook addressBook,
                                                      List<ReadOnlyPerson> displayList) {

        String expectedMessage = Messages.MESSAGE_PERSON_NOT_IN_ADDRESSBOOK;

        ChangeCommand command = createChangeCommand(visibleIndex, addressBook, displayList);
        assertCommandBehaviour(command, expectedMessage, addressBook, addressBook);
    }

    /**
     * Asserts that the person at the specified index can be successfully Changed.
     *
     * The addressBook passed in will not be modified (no side effects).
     *
     * @throws PersonNotFoundException if the selected person is not in the address book
     */
    private void assertChangeSuccessful(int targetVisibleIndex, AddressBook addressBook,
                                          List<ReadOnlyPerson> displayList) throws PersonNotFoundException {

        ReadOnlyPerson targetPerson = displayList.get(targetVisibleIndex - TextUi.DISPLAYED_INDEX_OFFSET);

        AddressBook expectedAddressBook = TestUtil.clone(addressBook);

        try {

            Person toChange = new Person(targetPerson.getName(), new Phone("777777", targetPerson.getPhone().isPrivate()), targetPerson.getEmail(),
                    targetPerson.getAddress(), targetPerson.getTags());
            expectedAddressBook.removePerson(targetPerson);
            expectedAddressBook.addPerson(toChange);
            String expectedMessage = String.format(ChangeCommand.MESSAGE_CHANGE_PERSON_SUCCESS, toChange);

            AddressBook actualAddressBook = TestUtil.clone(addressBook);

            ChangeCommand command = createChangeCommand(targetVisibleIndex, actualAddressBook, displayList);
            assertCommandBehaviour(command, expectedMessage, expectedAddressBook, actualAddressBook);
        } catch (UniquePersonList.DuplicatePersonException dpe) {
            System.out.println("Cannot add person");
        } catch (IllegalValueException ive) {
            throw new RuntimeException("Phone number is invalid");
        }

    }
}
