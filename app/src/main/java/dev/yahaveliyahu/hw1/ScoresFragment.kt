package dev.yahaveliyahu.hw1

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.location.Geocoder
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.Button
import android.widget.HorizontalScrollView
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.fragment.app.Fragment
import java.util.Locale

class ScoresFragment : Fragment() {

    private var listener: OnScoreSelectedListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Checks if the Activity hosting the fragment implements the interface (Listener)
        if (context is OnScoreSelectedListener) {
            listener = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    // Creates and returns the main view of the fragment
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_scores, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tableLayout = view.findViewById<TableLayout>(R.id.high_scores_table)
        val scores = MyHighScores.getScores(requireContext())

        // If there are no more records
        if (scores.isEmpty()) {
            val emptyRow = TableRow(requireContext())
            val message = makeCell("No records yet").apply {
                setTextColor(Color.BLACK)
                setTypeface(null, Typeface.ITALIC)
                val params = TableRow.LayoutParams()
                params.span = 4
                layoutParams = params
            }
            emptyRow.addView(message)
            tableLayout.addView(emptyRow)
        } else {
            // Add the record rows
            for ((index, score) in scores.withIndex()) {
                val row = TableRow(requireContext())

                // Column #
                val cellIndex = makeCell("${index + 1}")
                cellIndex.layoutParams = TableRow.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
                row.addView(cellIndex)

                val cellName = makeScrollableCell(score.name)
                val cellScore = makeWeightedCell("${score.score}")
                val cellLoc = makeWeightedCell("", ellipsize = true) // Empty location cell

                // Show the place name by clicking on the location cell
                cellLoc.setOnClickListener {
                    val geocoder = Geocoder(requireContext(), Locale.getDefault())
                    val list = geocoder.getFromLocation(score.latitude, score.longitude, 1)
                    val placeName = list?.firstOrNull()?.getAddressLine(0) ?: "Unknown location"
                    cellLoc.text = placeName
                    listener?.onScoreSelected(score.latitude, score.longitude)
                }

                row.addView(cellName)
                row.addView(cellScore)
                row.addView(cellLoc)

                // Border for each cell
                for (i in 0 until row.childCount) {
                    row.getChildAt(i).setBackgroundResource(R.drawable.cell_border)
                }
                tableLayout.addView(row)
            }
        }

        view.findViewById<Button>(R.id.scores_BTN_menu).apply {
            text = context.getString(R.string.back_to_menu)
            setOnClickListener {
                val intent = Intent(requireContext(), MenuActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            }
        }
    }

    // Columns with equal weight
    fun makeWeightedCell(text: String, ellipsize: Boolean = false): TextView {
        return makeCell(text).apply {
            val lp = TableRow.LayoutParams(0, WRAP_CONTENT, 1f)
            layoutParams = lp
            if (ellipsize) {
                maxLines = 1
                setHorizontallyScrolling(true)
                movementMethod = ScrollingMovementMethod.getInstance()
            }
        }
    }

    private fun makeCell(text: String): TextView {
        return TextView(requireContext()).apply {
            this.text = text
            setPadding(16, 8, 16, 8)
            setTextColor(Color.BLACK)
            gravity = Gravity.CENTER
        }
    }

    // Instead of makeWeightedCell for long names:
    fun makeScrollableCell(text: String): View {
        val textView = TextView(requireContext()).apply {
            this.text = text
            setPadding(16, 8, 16, 8)
            setTextColor(Color.BLACK)
            gravity = Gravity.CENTER_VERTICAL or Gravity.START
            maxLines = 1
            setHorizontallyScrolling(true)
            isSingleLine = true
            isFocusable = true
            isFocusableInTouchMode = true
            isClickable = true
            isLongClickable = true
        }

        return HorizontalScrollView(requireContext()).apply {
            layoutParams = TableRow.LayoutParams(0, WRAP_CONTENT, 1f)
            isHorizontalScrollBarEnabled = true
            addView(textView)
        }
    }

}



