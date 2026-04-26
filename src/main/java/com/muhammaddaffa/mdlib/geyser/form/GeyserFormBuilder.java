package com.muhammaddaffa.mdlib.geyser.form;

import org.geysermc.cumulus.form.CustomForm;
import org.geysermc.cumulus.form.ModalForm;
import org.geysermc.cumulus.form.SimpleForm;
import org.geysermc.cumulus.response.ModalFormResponse;
import org.geysermc.cumulus.response.SimpleFormResponse;
import org.geysermc.cumulus.util.FormImage;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;

import java.util.function.BiConsumer;

public class GeyserFormBuilder {

    /**
     * Creates a new SimpleForm builder.
     * Simple forms consist of a title, content, and multiple buttons.
     */
    @Contract(value = " -> new", pure = true)
    public static SimpleForm.@NonNull Builder simple() {
        return SimpleForm.builder();
    }

    /**
     * Creates a new ModalForm builder.
     * Modal forms consist of a title, content, and two buttons (True/False).
     */
    @Contract(value = " -> new", pure = true)
    public static ModalForm.@NonNull Builder modal() {
        return ModalForm.builder();
    }

    /**
     * Creates a new CustomForm builder.
     * Custom forms can contain inputs, sliders, steps, toggles, and dropdowns.
     */
    @Contract(value = " -> new", pure = true)
    public static CustomForm.@NonNull Builder custom() {
        return CustomForm.builder();
    }

    /**
     * Quickly builds a SimpleForm.
     */
    public static @NonNull SimpleForm buildSimple(String title, String content, BiConsumer<SimpleForm,
            SimpleFormResponse> handler, String @NonNull ... buttons) {
        SimpleForm.Builder builder = simple().title(title).content(content).validResultHandler(handler);
        for (String button : buttons) {
            builder.button(button);
        }
        return builder.build();
    }

    /**
     * Quickly builds a ModalForm.
     */
    public static @NonNull ModalForm buildModal(String title, String content, String yes,
                                                String no, BiConsumer<ModalForm, ModalFormResponse> handler) {
        return modal()
                .title(title)
                .content(content)
                .button1(yes)
                .button2(no)
                .validResultHandler(handler)
                .build();
    }

    /**
     * Creates a FormImage from a URL.
     */
    public static @NonNull FormImage urlImage(@NonNull String url) {
        return FormImage.of(FormImage.Type.URL, url);
    }

    /**
     * Creates a FormImage from a path.
     */
    public static @NonNull FormImage pathImage(@NonNull String path) {
        return FormImage.of(FormImage.Type.PATH, path);
    }

}