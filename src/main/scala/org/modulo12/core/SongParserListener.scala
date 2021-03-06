package org.modulo12.core

import org.jfugue.midi.MidiDictionary
import org.jfugue.parser.ParserListenerAdapter
import org.jfugue.theory.{ Chord, Note }
import org.modulo12.core.{ KeySignature, Scale, TimeSignature }

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

class SongParserListener extends ParserListenerAdapter {
  var temposBPM       = new mutable.ListBuffer[Int]()
  var instrumentNames = new mutable.HashSet[String]()
  var timeSignature   = Option.empty[TimeSignature]
  var keySignature    = Option.empty[KeySignature]
  var notes           = new mutable.ListBuffer[Note]
  var chords          = new mutable.ListBuffer[Chord]
  val lyrics          = new mutable.ListBuffer[String]
  var numBarLines     = 0

  override def onMarkerParsed(marker: String): Unit = super.onMarkerParsed(marker)

  override def onBarLineParsed(id: Long): Unit =
    numBarLines = numBarLines + 1

  override def onLyricParsed(lyric: String): Unit =
    lyrics.addOne(lyric)

  override def onNoteParsed(note: Note): Unit =
    notes.addOne(note)

  override def onChordParsed(chord: Chord): Unit =
    chords.addOne(chord)

  override def onKeySignatureParsed(key: Byte, scale: Byte): Unit = {
    val scaleType = scale.toInt match {
      case 0 => Scale.MAJOR
      case 1 => Scale.MINOR
      case _ => Scale.UNKNOWN
    }

    keySignature = Option(KeySignature(Key.rotateOnCircleOfFifths(key.toInt), scaleType))
  }

  override def onTempoChanged(tempoBPM: Int): Unit =
    temposBPM.addOne(tempoBPM)

  override def onInstrumentParsed(instrument: Byte): Unit = {
    val instrumentName = MidiDictionary.INSTRUMENT_BYTE_TO_STRING.get(instrument)
    instrumentNames.add(instrumentName)
  }

  override def onTimeSignatureParsed(numerator: Byte, powerOfTwo: Byte): Unit =
    timeSignature = Option(TimeSignature(numerator.toInt, powerOfTwo.toInt))
}
