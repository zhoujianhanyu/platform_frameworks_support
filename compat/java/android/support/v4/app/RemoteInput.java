/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.support.v4.app;

import static android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.annotation.RestrictTo;
import android.util.Log;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Helper for using the {@link android.app.RemoteInput}.
 */
public final class RemoteInput extends RemoteInputCompatBase.RemoteInput {
    private static final String TAG = "RemoteInput";

    /** Label used to denote the clip data type used for remote input transport */
    public static final String RESULTS_CLIP_LABEL = "android.remoteinput.results";

    /** Extra added to a clip data intent object to hold the text results bundle. */
    public static final String EXTRA_RESULTS_DATA = "android.remoteinput.resultsData";

    /** Extra added to a clip data intent object to hold the data results bundle. */
    private static final String EXTRA_DATA_TYPE_RESULTS_DATA =
            "android.remoteinput.dataTypeResultsData";

    private final String mResultKey;
    private final CharSequence mLabel;
    private final CharSequence[] mChoices;
    private final boolean mAllowFreeFormTextInput;
    private final Bundle mExtras;
    private final Set<String> mAllowedDataTypes;

    RemoteInput(String resultKey, CharSequence label, CharSequence[] choices,
            boolean allowFreeFormTextInput, Bundle extras, Set<String> allowedDataTypes) {
        this.mResultKey = resultKey;
        this.mLabel = label;
        this.mChoices = choices;
        this.mAllowFreeFormTextInput = allowFreeFormTextInput;
        this.mExtras = extras;
        this.mAllowedDataTypes = allowedDataTypes;
    }

    /**
     * Get the key that the result of this input will be set in from the Bundle returned by
     * {@link #getResultsFromIntent} when the {@link android.app.PendingIntent} is sent.
     */
    @Override
    public String getResultKey() {
        return mResultKey;
    }

    /**
     * Get the label to display to users when collecting this input.
     */
    @Override
    public CharSequence getLabel() {
        return mLabel;
    }

    /**
     * Get possible input choices. This can be {@code null} if there are no choices to present.
     */
    @Override
    public CharSequence[] getChoices() {
        return mChoices;
    }

    @Override
    public Set<String> getAllowedDataTypes() {
        return mAllowedDataTypes;
    }

    /**
     * Returns true if the input only accepts data, meaning {@link #getAllowFreeFormInput}
     * is false, {@link #getChoices} is null or empty, and {@link #getAllowedDataTypes is
     * non-null and not empty.
     */
    public boolean isDataOnly() {
        return !getAllowFreeFormInput()
                && (getChoices() == null || getChoices().length == 0)
                && getAllowedDataTypes() != null
                && !getAllowedDataTypes().isEmpty();
    }

    /**
     * Get whether or not users can provide an arbitrary value for
     * input. If you set this to {@code false}, users must select one of the
     * choices in {@link #getChoices}. An {@link IllegalArgumentException} is thrown
     * if you set this to false and {@link #getChoices} returns {@code null} or empty.
     */
    @Override
    public boolean getAllowFreeFormInput() {
        return mAllowFreeFormTextInput;
    }

    /**
     * Get additional metadata carried around with this remote input.
     */
    @Override
    public Bundle getExtras() {
        return mExtras;
    }

    /**
     * Builder class for {@link android.support.v4.app.RemoteInput} objects.
     */
    public static final class Builder {
        private final String mResultKey;
        private CharSequence mLabel;
        private CharSequence[] mChoices;
        private boolean mAllowFreeFormTextInput = true;
        private Bundle mExtras = new Bundle();
        private final Set<String> mAllowedDataTypes = new HashSet<>();

        /**
         * Create a builder object for {@link android.support.v4.app.RemoteInput} objects.
         * @param resultKey the Bundle key that refers to this input when collected from the user
         */
        public Builder(String resultKey) {
            if (resultKey == null) {
                throw new IllegalArgumentException("Result key can't be null");
            }
            mResultKey = resultKey;
        }

        /**
         * Set a label to be displayed to the user when collecting this input.
         * @param label The label to show to users when they input a response.
         * @return this object for method chaining
         */
        public Builder setLabel(CharSequence label) {
            mLabel = label;
            return this;
        }

        /**
         * Specifies choices available to the user to satisfy this input.
         * @param choices an array of pre-defined choices for users input.
         *        You must provide a non-null and non-empty array if
         *        you disabled free form input using {@link #setAllowFreeFormInput}.
         * @return this object for method chaining
         */
        public Builder setChoices(CharSequence[] choices) {
            mChoices = choices;
            return this;
        }

        /**
         * Specifies whether the user can provide arbitrary values.
         *
         * @param mimeType A mime type that results are allowed to come in.
         *         Be aware that text results (see {@link #setAllowFreeFormInput}
         *         are allowed by default. If you do not want text results you will have to
         *         pass false to {@code setAllowFreeFormInput}.
         * @param doAllow Whether the mime type should be allowed or not.
         * @return this object for method chaining
         */
        public Builder setAllowDataType(String mimeType, boolean doAllow) {
            if (doAllow) {
                mAllowedDataTypes.add(mimeType);
            } else {
                mAllowedDataTypes.remove(mimeType);
            }
            return this;
        }

        /**
         * Specifies whether the user can provide arbitrary text values.
         *
         * @param allowFreeFormTextInput The default is {@code true}.
         *         If you specify {@code false}, you must either provide a non-null
         *         and non-empty array to {@link #setChoices}, or enable a data result
         *         in {@code setAllowDataType}. Otherwise an
         *         {@link IllegalArgumentException} is thrown.
         * @return this object for method chaining
         */
        public Builder setAllowFreeFormInput(boolean allowFreeFormTextInput) {
            mAllowFreeFormTextInput = allowFreeFormTextInput;
            return this;
        }

        /**
         * Merge additional metadata into this builder.
         *
         * <p>Values within the Bundle will replace existing extras values in this Builder.
         *
         * @see RemoteInput#getExtras
         */
        public Builder addExtras(Bundle extras) {
            if (extras != null) {
                mExtras.putAll(extras);
            }
            return this;
        }

        /**
         * Get the metadata Bundle used by this Builder.
         *
         * <p>The returned Bundle is shared with this Builder.
         */
        public Bundle getExtras() {
            return mExtras;
        }

        /**
         * Combine all of the options that have been set and return a new
         * {@link android.support.v4.app.RemoteInput} object.
         */
        public RemoteInput build() {
            return new RemoteInput(
                    mResultKey,
                    mLabel,
                    mChoices,
                    mAllowFreeFormTextInput,
                    mExtras,
                    mAllowedDataTypes);
        }
    }

    /**
     * Similar as {@link #getResultsFromIntent} but retrieves data results for a
     * specific RemoteInput result. To retrieve a value use:
     * <pre>
     * {@code
     * Map<String, Uri> results =
     *     RemoteInput.getDataResultsFromIntent(intent, REMOTE_INPUT_KEY);
     * if (results != null) {
     *   Uri data = results.get(MIME_TYPE_OF_INTEREST);
     * }
     * }
     * </pre>
     * @param intent The intent object that fired in response to an action or content intent
     *               which also had one or more remote input requested.
     * @param remoteInputResultKey The result key for the RemoteInput you want results for.
     */
    public static Map<String, Uri> getDataResultsFromIntent(
            Intent intent, String remoteInputResultKey) {
        return IMPL.getDataResultsFromIntent(intent, remoteInputResultKey);
    }

    /**
     * Get the remote input text results bundle from an intent. The returned Bundle will
     * contain a key/value for every result key populated by remote input collector.
     * Use the {@link Bundle#getCharSequence(String)} method to retrieve a value. For data results
     * use {@link #getDataResultsFromIntent}.
     * @param intent The intent object that fired in response to an action or content intent
     *               which also had one or more remote input requested.
     */
    public static Bundle getResultsFromIntent(Intent intent) {
        return IMPL.getResultsFromIntent(intent);
    }

    /**
     * Populate an intent object with the results gathered from remote input. This method
     * should only be called by remote input collection services when sending results to a
     * pending intent.
     * @param remoteInputs The remote inputs for which results are being provided
     * @param intent The intent to add remote inputs to. The {@link android.content.ClipData}
     *               field of the intent will be modified to contain the results.
     * @param results A bundle holding the remote input results. This bundle should
     *                be populated with keys matching the result keys specified in
     *                {@code remoteInputs} with values being the result per key.
     */
    public static void addResultsToIntent(RemoteInput[] remoteInputs, Intent intent,
            Bundle results) {
        IMPL.addResultsToIntent(remoteInputs, intent, results);
    }

    /**
     * Same as {@link #addResultsToIntent} but for setting data results.
     * @param remoteInput The remote input for which results are being provided
     * @param intent The intent to add remote input results to. The
     *               {@link android.content.ClipData} field of the intent will be
     *               modified to contain the results.
     * @param results A map of mime type to the Uri result for that mime type.
     */
    public static void addDataResultToIntent(RemoteInput remoteInput, Intent intent,
            Map<String, Uri> results) {
        IMPL.addDataResultToIntent(remoteInput, intent, results);
    }

    private static final Impl IMPL;

    interface Impl {
        Bundle getResultsFromIntent(Intent intent);
        Map<String, Uri> getDataResultsFromIntent(Intent intent, String remoteInputResultKey);
        void addResultsToIntent(RemoteInput[] remoteInputs, Intent intent,
                Bundle results);
        void addDataResultToIntent(RemoteInput remoteInput, Intent intent,
                Map<String, Uri> results);
    }

    static class ImplBase implements Impl {
        @Override
        public Bundle getResultsFromIntent(Intent intent) {
            Log.w(TAG, "RemoteInput is only supported from API Level 16");
            return null;
        }

        @Override
        public Map<String, Uri> getDataResultsFromIntent(
                Intent intent, String remoteInputResultKey) {
            Log.w(TAG, "RemoteInput is only supported from API Level 16");
            return null;
        }

        @Override
        public void addResultsToIntent(RemoteInput[] remoteInputs, Intent intent, Bundle results) {
            Log.w(TAG, "RemoteInput is only supported from API Level 16");
        }

        @Override
        public void addDataResultToIntent(RemoteInput remoteInput, Intent intent,
                Map<String, Uri> results) {
            Log.w(TAG, "RemoteInput is only supported from API Level 16");
        }
    }

    @RequiresApi(16)
    static class ImplJellybean implements Impl {
        @Override
        public Bundle getResultsFromIntent(Intent intent) {
            return RemoteInputCompatJellybean.getResultsFromIntent(intent);
        }

        @Override
        public Map<String, Uri> getDataResultsFromIntent(
                Intent intent, String remoteInputResultKey) {
            return RemoteInputCompatJellybean.getDataResultsFromIntent(
                    intent, remoteInputResultKey);
        }

        @Override
        public void addResultsToIntent(RemoteInput[] remoteInputs, Intent intent, Bundle results) {
            RemoteInputCompatJellybean.addResultsToIntent(remoteInputs, intent, results);
        }

        @Override
        public void addDataResultToIntent(RemoteInput remoteInput, Intent intent,
                Map<String, Uri> results) {
            RemoteInputCompatJellybean.addDataResultToIntent(remoteInput, intent, results);
        }
    }

    @RequiresApi(20)
    static class ImplApi20 implements Impl {
        @Override
        public Bundle getResultsFromIntent(Intent intent) {
            return RemoteInputCompatApi20.getResultsFromIntent(intent);
        }

        @Override
        public Map<String, Uri> getDataResultsFromIntent(
                Intent intent, String remoteInputResultKey) {
            return RemoteInputCompatApi20.getDataResultsFromIntent(intent, remoteInputResultKey);
        }

        @Override
        public void addResultsToIntent(RemoteInput[] remoteInputs, Intent intent, Bundle results) {
            RemoteInputCompatApi20.addResultsToIntent(remoteInputs, intent, results);
        }

        @Override
        public void addDataResultToIntent(RemoteInput remoteInput, Intent intent,
                Map<String, Uri> results) {
            RemoteInputCompatApi20.addDataResultToIntent(remoteInput, intent, results);
        }
    }

    static {
        if (Build.VERSION.SDK_INT >= 20) {
            IMPL = new ImplApi20();
        } else if (Build.VERSION.SDK_INT >= 16) {
            IMPL = new ImplJellybean();
        } else {
            IMPL = new ImplBase();
        }
    }

    /** @hide */
    @RestrictTo(LIBRARY_GROUP)
    public static final Factory FACTORY = new Factory() {
        @Override
        public RemoteInput build(String resultKey,
                CharSequence label, CharSequence[] choices, boolean allowFreeFormInput,
                Bundle extras, Set<String> allowedDataTypes) {
            return new RemoteInput(
                    resultKey, label, choices, allowFreeFormInput, extras, allowedDataTypes);
        }

        @Override
        public RemoteInput[] newArray(int size) {
            return new RemoteInput[size];
        }
    };
}
