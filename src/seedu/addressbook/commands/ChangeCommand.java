package seedu.addressbook.commands;

import java.util.*;

import seedu.addressbook.common.Messages;
import seedu.addressbook.data.exception.IllegalValueException;
import seedu.addressbook.data.person.Address;
import seedu.addressbook.data.person.Email;
import seedu.addressbook.data.person.Name;
import seedu.addressbook.data.person.Person;
import seedu.addressbook.data.person.Phone;
import seedu.addressbook.data.person.ReadOnlyPerson;
import seedu.addressbook.data.person.UniquePersonList;
import seedu.addressbook.data.tag.Tag;
import seedu.addressbook.commands.FindCommand;
import seedu.addressbook.commands.AddCommand;
import java.util.Scanner;

/**
 * Changes a person phone number by using their index.
 */
public class ChangeCommand extends Command {

    public static final String COMMAND_WORD = "change";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ":Changes the phone number of a person in the addressbook. "
            + "Example: " + COMMAND_WORD
            + " John Doe 91234567";

    public static final String MESSAGE_SUCCESS = "Person number changed: %1$s";
    public static final String MESSAGE_DUPLICATE_PERSON = "This person already exists in the address book";

    public ChangeCommand(int targetVisibleIndex ) {
        super(targetVisibleIndex);
    }




    @Override
    public CommandResult execute() {
        try {
            Scanner sc = new Scanner(System.in);
            final ReadOnlyPerson targetPerson = getTargetPerson();
            System.out.print("|| Enter new Number: ");
            String newNumber = sc.nextLine();
            Person toChange = new Person(targetPerson.getName(), new Phone(newNumber, targetPerson.getPhone().isPrivate()),
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
        }
    }
}
