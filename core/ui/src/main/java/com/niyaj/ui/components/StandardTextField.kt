package com.niyaj.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Checkbox
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.niyaj.core.ui.R
import com.niyaj.designsystem.theme.HintGray
import com.niyaj.designsystem.theme.SpaceMini

@Composable
fun StandardOutlinedTextField(
    modifier : Modifier = Modifier,
    text : String,
    label : String,
    error : String? = null,
    style : TextStyle = TextStyle(
        color = MaterialTheme.colors.onBackground
    ),
    singleLine : Boolean = true,
    minLines: Int = 1,
    maxLines : Int = 1,
    leadingIcon : ImageVector? = null,
    keyboardType : KeyboardType = KeyboardType.Text,
    isPasswordToggleDisplayed : Boolean = keyboardType == KeyboardType.Password,
    isPasswordVisible : Boolean = false,
    onPasswordToggleClick : (Boolean) -> Unit = {},
    onValueChange : (String) -> Unit,
    trailingIcon : @Composable () -> Unit = {},
    readOnly : Boolean = false,
    message : String? = null,
    errorTag : String = "",
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = text,
            readOnly = readOnly,
            label = {
                Text(text = label)
            },
            onValueChange = {
                onValueChange(it)
            },
            maxLines = maxLines,
            minLines = minLines,
            textStyle = style,
            placeholder = {
                Text(
                    text = label,
                    style = MaterialTheme.typography.body1
                )
            },
            isError = !error.isNullOrEmpty(),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                keyboardType = keyboardType,
            ),
            visualTransformation = if (!isPasswordVisible && isPasswordToggleDisplayed) {
                PasswordVisualTransformation()
            } else {
                VisualTransformation.None
            },
            singleLine = singleLine,
            leadingIcon = if (leadingIcon != null) {
                val icon : @Composable () -> Unit = {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = null,
                    )
                }
                icon
            } else null,
            trailingIcon = if (isPasswordToggleDisplayed) {
                val icon : @Composable () -> Unit = {
                    IconButton(
                        onClick = {
                            onPasswordToggleClick(!isPasswordVisible)
                        },
                        modifier = Modifier
                    ) {
                        Icon(
                            imageVector = if (isPasswordVisible) {
                                Icons.Filled.VisibilityOff
                            } else {
                                Icons.Filled.Visibility
                            },
                            tint = MaterialTheme.colors.secondaryVariant,
                            contentDescription = if (isPasswordVisible) {
                                stringResource(id = R.string.password_visible_content_description)
                            } else {
                                stringResource(id = R.string.password_hidden_content_description)
                            }
                        )
                    }
                }
                icon
            } else trailingIcon,
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(4.dp)
        )

        if (error != null) {
            Text(
                text = error,
                style = MaterialTheme.typography.body2,
                color = MaterialTheme.colors.error,
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .testTag(errorTag)
                    .fillMaxWidth()
                    .padding(top = SpaceMini)
            )
        } else if (message != null) {
            Text(
                text = message,
                style = MaterialTheme.typography.caption,
                color = HintGray,
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .testTag(errorTag)
                    .fillMaxWidth()
            )
        }
    }
}


@Composable
fun StandardTextField(
    modifier : Modifier = Modifier,
    text : String = "",
    hint : String = "",
    maxLength : Int = 40,
    error : String? = null,
    style : TextStyle = TextStyle(
        color = MaterialTheme.colors.onBackground
    ),
    singleLine : Boolean = true,
    maxLines : Int = 1,
    leadingIcon : ImageVector? = null,
    keyboardType : KeyboardType = KeyboardType.Text,
    isPasswordToggleDisplayed : Boolean = keyboardType == KeyboardType.Password,
    isPasswordVisible : Boolean = false,
    onPasswordToggleClick : (Boolean) -> Unit = {},
    onValueChange : (String) -> Unit,
    trailingIcon : @Composable () -> Unit = {},
    readOnly : Boolean = false,
    message : String? = null,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        TextField(
            value = text,
            readOnly = readOnly,
            label = {
                Text(text = hint)
            },
            onValueChange = {
                if (it.length <= maxLength) {
                    onValueChange(it)
                }
            },
            maxLines = maxLines,
            textStyle = style,
            placeholder = {
                Text(
                    text = hint,
                    style = MaterialTheme.typography.body1
                )
            },
            isError = !error.isNullOrEmpty(),
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType
            ),
            visualTransformation = if (!isPasswordVisible && isPasswordToggleDisplayed) {
                PasswordVisualTransformation()
            } else {
                VisualTransformation.None
            },
            singleLine = singleLine,
            leadingIcon = if (leadingIcon != null) {
                val icon : @Composable () -> Unit = {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = null,
                        tint = MaterialTheme.colors.onBackground,
                    )
                }
                icon
            } else null,
            trailingIcon = if (isPasswordToggleDisplayed) {
                val icon : @Composable () -> Unit = {
                    IconButton(
                        onClick = {
                            onPasswordToggleClick(!isPasswordVisible)
                        },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Icon(
                            imageVector = if (isPasswordVisible) {
                                Icons.Filled.VisibilityOff
                            } else {
                                Icons.Filled.Visibility
                            },
                            tint = Color.Blue,
                            contentDescription = if (isPasswordVisible) {
                                stringResource(id = R.string.password_visible_content_description)
                            } else {
                                stringResource(id = R.string.password_hidden_content_description)
                            }
                        )
                    }
                }
                icon
            } else trailingIcon,
            modifier = modifier,
        )

        if (error != null) {
            Text(
                text = error,
                style = MaterialTheme.typography.body2,
                color = MaterialTheme.colors.error,
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .fillMaxWidth()
            )
        } else if (message != null) {
            Text(
                text = message,
                style = MaterialTheme.typography.caption,
                color = MaterialTheme.colors.error,
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
    }
}

@Composable
fun DropdownTextField(
    modifier : Modifier = Modifier,
    value : String = "",
    onValueChange : (String) -> Unit = {},
    label : String = "",
    readOnly : Boolean = true,
    trailingIcon : @Composable (() -> Unit)? = null,
    error : String? = null,
    message : String? = null
) {
    Column {
        TextField(
            readOnly = readOnly,
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    text = label,
                    style = MaterialTheme.typography.body1
                )
            },
            label = {
                Text(text = label)
            },
            trailingIcon = trailingIcon,
            isError = !error.isNullOrEmpty(),
            modifier = modifier
                .fillMaxWidth(),
        )

        if (error != null) {
            Text(
                text = error,
                style = MaterialTheme.typography.body2,
                color = MaterialTheme.colors.error,
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .fillMaxWidth()
            )
        } else if (message != null) {
            Text(
                text = message,
                style = MaterialTheme.typography.caption,
                color = MaterialTheme.colors.error,
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
    }
}

@Composable
fun StandardCheckboxWithText(
    modifier : Modifier = Modifier,
    text : String,
    checked : Boolean,
    onCheckedChange : () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
    ) {
        Checkbox(checked = checked, onCheckedChange = { onCheckedChange() })

        Text(
            text = text,
            style = MaterialTheme.typography.overline
        )
    }
}