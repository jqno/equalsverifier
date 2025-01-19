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

        new ClassReader(moduleinfo).accept(new ClassVisitor(Opcodes.ASM9) {
            @Override
            public ModuleVisitor visitModule(String name, int access, String version) {
                result.moduleName = name;
                return new ModuleVisitor(Opcodes.ASM9) {
                    @Override
                    public void visitExport(String packaze, int access, String... modules) {
                        result.exports.add(packaze);
                    }

                    @Override
                    public void visitRequire(String module, int access, String version) {
                        var modifiers = (access & Opcodes.ACC_TRANSITIVE) != 0 ? "transitive" : "";
                        result.requires.put(module, modifiers);
                    }
                };
            }
        }, 0);

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

}
