package com.gmail.ikeike443;

import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * 2012-03-21
 */
public class PlayAutoTestBuilderUnitTest {

    @Test
    public void command_parsing_in_the_constructor() {
        PlayAutoTestBuilder builder = new PlayAutoTestBuilder("auto-test", null, null, null, null, null);
        assertThat(builder.ensureCommandString("restart"), is(equalTo("restart")));
        assertThat(builder.ensureCommandString("    restart "), is(equalTo("restart")));
        assertThat(builder.ensureCommandString(""), is(equalTo("")));
        assertThat(builder.ensureCommandString(null), is(equalTo("")));
    }

    @Test
    public void the_commands_list_should_contain_the_non_empty_commands() {
        PlayAutoTestBuilder builder = new PlayAutoTestBuilder("auto-test", null, "restart --deps", null, "stop", "/usr/local/play");
        List<String> commands = builder.nonEmptyCommands();
        assertThat(commands.size(), is(3));
        assertThat(commands.get(0), is(equalTo("auto-test")));
        assertThat(commands.get(1), is(equalTo("restart --deps")));
        assertThat(commands.get(2), is(equalTo("stop")));
    }
}
