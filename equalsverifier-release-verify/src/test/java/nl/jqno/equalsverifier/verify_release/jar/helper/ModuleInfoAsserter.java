package nl.jqno.equalsverifier.verify_release.jar.helper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.*;

import net.bytebuddy.jar.asm.*;

public class ModuleInfoAsserter {

    private String moduleName;
    private final Set<String> exports = new HashSet<>();
    private final Map<String, String> requires = new HashMap<>();

    public static ModuleInfoAsserter parse(byte[] moduleinfo) {
        var result = new ModuleInfoAsserter();
        new ClassReader(moduleinfo).accept(new ModuleInfoParser(result), 0);
        return result;
    }

    public void assertName(String name) {
        assertThat(moduleName).isEqualTo(name);
    }

    public void assertExports(String... names) {
        assertThat(exports).containsOnly(names);
    }

    public void assertRequires(String require, String modifier) {
        assertThat(requires).containsEntry(require, modifier);
    }

    public void assertDoesNotRequire(String require) {
        assertThat(requires).doesNotContainKey(require);
    }

    private static final class ModuleInfoParser extends ClassVisitor {

        private final ModuleInfoAsserter asserter;

        private ModuleInfoParser(ModuleInfoAsserter asserter) {
            super(Opcodes.ASM9);
            this.asserter = asserter;
        }

        @Override
        public ModuleVisitor visitModule(String name, int access, String version) {
            asserter.moduleName = name;
            return new ModuleVisitorExtension(asserter);
        }
    }

    private static final class ModuleVisitorExtension extends ModuleVisitor {
        private final ModuleInfoAsserter asserter;

        private ModuleVisitorExtension(ModuleInfoAsserter asserter) {
            super(Opcodes.ASM9);
            this.asserter = asserter;
        }

        @Override
        public void visitExport(String packaze, int access, String... modules) {
            asserter.exports.add(packaze);
        }

        @Override
        public void visitRequire(String module, int access, String version) {
            var modifiers = "";
            if ((access & Opcodes.ACC_TRANSITIVE) != 0) {
                modifiers += "transitive";
            }
            if ((access & Opcodes.ACC_STATIC_PHASE) != 0) {
                modifiers += "static";
            }
            if ((access & Opcodes.ACC_SYNTHETIC) != 0) {
                modifiers += "synthetic";
            }
            if ((access & Opcodes.ACC_MANDATED) != 0) {
                modifiers += "mandated";
            }
            asserter.requires.put(module, modifiers);
        }
    }
}
