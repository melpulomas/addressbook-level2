package seedu.addressbook.commands;

import seedu.addressbook.common.Messages;
import seedu.addressbook.data.exception.IllegalValueException;

import seedu.addressbook.data.person.Person;
import seedu.addressbook.data.person.Phone;
import seedu.addressbook.data.person.ReadOnlyPerson;
import seedu.addressbook.data.person.UniquePersonList;

import java.util.Scanner;

/**
 * Changes a person phone number by using their index.
 */
public class ChangeCommand extends Command {

    public static final String COMMAND_WORD = "change";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ":Changes the phone number of a person in the addressbook. "
            + "Example: " + COMMAND_WORD
            + " John Doe 91234567";
    public static final String MESSAGE_CHANGE_PERSON_SUCCESS = "Person number changed: %1$s";
    public static final String MESSAGE_SUCCESS = "Person number changed: %1$s";
    public static final String MESSAGE_DUPLICATE_PERSON = "This person already exists in the address book";

    private String newNumber;

    public ChangeCommand(int targetVisibleIndex, String newNumber ) {
        super(targetVisibleIndex);
        this.newNumber = newNumber;
    }




    @Override
    public CommandResult execute() {
        try {

            final ReadOnlyPerson targetPerson = getTargetPerson();

            Person toChange = new Person(targetPerson.getName(), new Phone(this.newNumber, targetPerson.getPhone().isPrivate()),
                    targetPerson.getEmail(), targetPerson.getAddress(), targetPerson.getTags());
            addressBook.removePerson(targetPerson);
            addressBook.addPerson(toChange);
            return new CommandResult(String.format(MESSAGE_SUCCESS, toChange));
        } catch (UniquePersonList.PersonNotFoundException pnfe) {
            return new CommandResult(Messages.MESSAGE_PERSON_NOT_IN_ADDRESSBOOK);
        } catch (UniquePersonList.DuplicatePersonException dpe) {
            return new CommandResult(MESSAGE_DUPLICATE_PERSON);
        } catch (IllegalValueException ive) {
            throw new RuntimeException("Phone number is invalid");
        } catch (IndexOutOfBoundsException ie) {
            return new CommandResult(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }
    }
}
